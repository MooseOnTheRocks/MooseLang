package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interpreter {
    public final IRComp term;
    public final List<Object> stack;
    public final Scope scope;
    public final boolean terminated;

    public Interpreter(IRComp term, List<Object> stack, Scope scope, boolean terminated) {
        this.term = term;
        this.stack = List.copyOf(stack);
        this.scope = scope;
        this.terminated = terminated;
    }

    public Interpreter execute() {
        var state = this;
        while (!state.terminated) {
            state = state.stepExecution();
        }
        return state;
    }

    public Interpreter stepExecution() {
        if (terminated) {
            return this;
        }

//        System.out.println("== STEP");
//        System.out.println("Term: " + term);
//        System.out.println("Stack: " + stack);
//        System.out.println("Scope: " + scope);
//        System.out.println("---");

        if (term instanceof IRBuiltin builtin) return step(builtin);
        else if (term instanceof IRLet let) return step(let);
        else if (term instanceof IRDo let) return step(let);
        else if (term instanceof IRProduce produce) return step(produce);
        else if (term instanceof IRForce force) return step(force);
        else if (term instanceof IRPush push) return step(push);
        else if (term instanceof IRLambda lambda) return step(lambda);
        else if (term instanceof IRCaseOf caseOf) return step(caseOf);
        else {
            System.err.println("Unhandled term: " + term);
            return new Interpreter(term, stack, scope, true);
        }
    }

    public Interpreter step(IRBuiltin builtin) {
        return builtin.internal.apply(this);
    }

    public Interpreter step(IRCaseOf caseOf) {
        var value = caseOf.value;
        if (value instanceof IRName valueName) {
            var maybeName = scope.find(valueName.name);
            if (maybeName.isEmpty()) {
                System.err.println("Cannot find " + valueName.name + " in scope");
                return new Interpreter(term, stack, scope, true);
            }
            value = maybeName.get();
        }

        for (var branch : caseOf.branches) {
            if (patternMatches(value, branch.pattern)) {
                var interp = destructPattern(value, branch.pattern);
                return new Interpreter(branch.body, interp.stack, interp.scope, interp.terminated);
            }
        }

        System.err.println("Unhandled case-of: " + caseOf);
        return new Interpreter(term, stack, scope, true);

//        if (value instanceof IRTuple tuple && caseOf.branches.size() == 1) {
//            var branch = caseOf.branches.get(0);
//            boolean good = branch.pattern instanceof IRTuple pattern &&
//                pattern.values.size() == tuple.values.size() &&
//                pattern.values.stream().allMatch(v -> v instanceof IRName);
//            if (!(branch.pattern instanceof IRTuple pattern) || !good) {
//                System.err.println("Bad branch pattern: " + branch.pattern);
//                return new Interpreter(term, stack, scope, true);
//            }
//            var names = pattern.values.stream().map(v -> ((IRName) v).name).toList();
//            var newScope = scope;
//            for (int i = 0; i < names.size(); i++) {
//                newScope = newScope.put(names.get(i), tuple.values.get(i));
//            }
//            return new Interpreter(branch.body, stack, newScope, false);
//        }
//        else {
//            System.err.println("Unhandled case-of: " + caseOf);
//            return new Interpreter(term, stack, scope, true);
//        }
    }

    public boolean patternMatches(IRValue value, IRValue pattern) {
        if (pattern instanceof IRName) {
            return true;
        }
        else if (value instanceof IRNumber valueNum && pattern instanceof IRNumber patternNum) {
            return valueNum.value == patternNum.value;
        }
        else if (value instanceof IRString valueString && pattern instanceof IRString patternString) {
            return valueString.value.equals(patternString.value);
        }
        else if (value instanceof IRUnit && pattern instanceof IRUnit) {
            return true;
        }
        else {
            return false;
        }
    }

    public Interpreter destructPattern(IRValue value, IRValue pattern) {
        if (!patternMatches(value, pattern)) {
            System.err.println("Cannot destruct " + value + ", on pattern: " + pattern);
            return new Interpreter(term, stack, scope, true);
        }

        if (pattern instanceof IRName patternName) {
            return new Interpreter(term, stack, scope.put(patternName.name, value), false);
        }
        else {
            return this;
        }
    }

    public Interpreter step(IRLambda lambda) {
        if (stack.isEmpty()) {
            return new Interpreter(lambda, stack, scope, true);
        }

        var top = stack.get(stack.size() - 1);
        if (top instanceof IRValue value) {
            var newStack = new ArrayList<>(stack);
            newStack.remove(newStack.size() - 1);
            return new Interpreter(lambda.body, newStack, scope.put(lambda.paramName, value), false);
        }
        else {
            System.err.println("Lambda expects value on the stack, received: " + top);
            return new Interpreter(term, stack, scope, true);
        }
    }

    public Interpreter step(IRPush push) {
        var newStack = new ArrayList<>(stack);
        if (push.value instanceof IRName name) {
            newStack.add(scope.find(name.name).get());
        }
        else {
            newStack.add(push.value);
        }
        return new Interpreter(push.then, newStack, scope, false);
    }

    public Interpreter step(IRForce force) {
        IRThunk thunk = null;
        if (force.thunk instanceof IRName name) {
            var mbound = scope.find(name.name);
            if (mbound.isEmpty()) {
                System.err.println("Cannot find " + name.name + " in scope");
                return new Interpreter(term, stack, scope, true);
            }
            else if (mbound.get() instanceof IRThunk irThunk) {
                thunk = irThunk;
            }
            else {
                System.err.println("Force expected thunk, (or named thunk), received: " + force.thunk);
                return new Interpreter(term, stack, scope, true);
            }
        }
        else if (force.thunk instanceof IRThunk irThunk) {
            thunk = irThunk;
        }
        else {
            System.err.println("Force expected thunk, received: " + force.thunk);
            return new Interpreter(term, stack, scope, true);
        }

        return new Interpreter(thunk.comp, stack, scope, false);
    }

    public Interpreter step(IRProduce produce) {
        // If producing identifier, replace with value from scope.
        if (produce.value instanceof IRName name) {
            var mbound = scope.find(name.name);
            if (mbound.isEmpty()) {
                System.err.println("Cannot find " + name.name + " in scope");
                return new Interpreter(term, stack, scope, true);
            }
            produce = new IRProduce(mbound.get());
        }
        // Replace names in Tuple
        else if (produce.value instanceof IRTuple tuple) {
            var newValues = new ArrayList<>(tuple.values);
            for (int i = 0; i < tuple.values.size(); i++) {
                IRValue v = tuple.values.get(i);
                if (v instanceof IRName name) {
                    var mbound = scope.find(name.name);
                    if (mbound.isEmpty()) {
                        System.err.println("Cannot find " + name.name + " in scope");
                        return new Interpreter(term, stack, scope, true);
                    }
                    newValues.set(i, mbound.get());
                }
            }
            produce = new IRProduce(new IRTuple(newValues));
        }

        if (stack.isEmpty()) {
            // Terminal configuration if stack is empty.
            return new Interpreter(produce, stack, scope, true);
        }
        var top = stack.get(stack.size() - 1);
        if (top instanceof IRDo let) {
            var newStack = new ArrayList<>(stack);
            newStack.remove(stack.size() - 1);
            return new Interpreter(let.body, newStack, scope.put(let.name, produce.value), false);
        }
        else {
            System.err.println("Unable to produce!");
            return new Interpreter(term, stack, scope, true);
        }
    }

    public Interpreter step(IRLet let) {
        var name = let.name;
        var value = let.value;
        var body = let.body;

        return new Interpreter(body, stack, scope.put(name, value), false);
    }

    public Interpreter step(IRDo let) {
        var name = let.name;
        var expr = let.boundComp;
        var body = let.body;
        var newStack = new ArrayList<>(stack);
        newStack.add(let);
        return new Interpreter(expr, newStack, scope, false);
    }

    @Override
    public String toString() {
        return "Interpreter(" + term + ", " + terminated + ", " + stack + ", " + scope.allBindings(Map.of()) + ")";
    }
}
