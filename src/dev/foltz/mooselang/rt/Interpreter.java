package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    public Interpreter stepExecution() {
        if (terminated) {
            return this;
        }

        if (term instanceof IRLetValue let) return step(let);
        else if (term instanceof IRLetComp let) return step(let);
        else if (term instanceof IRProduce produce) return step(produce);
        else if (term instanceof IRForceName force) return step(force);
        else if (term instanceof IRPush push) return step(push);
        else {
            System.err.println("Unhandled term: " + term);
            return new Interpreter(term, stack, scope, true);
        }
    }

    public Interpreter step(IRPush push) {
        throw new RuntimeException("Unsupported push");
    }

    public Interpreter step(IRForceName force) {
        var mbound = scope.find(force.name);
        if (mbound.isEmpty()) {
            System.err.println("Cannot find " + force.name + " in scope");
            return new Interpreter(term, stack, scope, true);
        }

        var bound = mbound.get();
        if (bound instanceof IRThunk thunk) {
            return new Interpreter(thunk.comp, stack, scope, false);
        }
        else {
            System.err.println("Force expected thunk, received: " + bound);
            return new Interpreter(term, stack, scope, true);
        }
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

        if (stack.isEmpty()) {
            // Terminal configuration if stack is empty.
            return new Interpreter(produce, stack, scope, true);
        }
        var top = stack.get(stack.size() - 1);
        if (top instanceof IRLetComp let) {
            var newStack = new ArrayList<>(stack);
            newStack.remove(stack.size() - 1);
            return new Interpreter(let.body, newStack, scope.put(let.name, produce.value), false);
        }
        else {
            System.err.println("Unable to produce!");
            return new Interpreter(term, stack, scope, true);
        }
    }

    public Interpreter step(IRLetValue let) {
        var name = let.name;
        var value = let.value;
        var body = let.body;

        return new Interpreter(body, stack, scope.put(name, value), false);
    }

    public Interpreter step(IRLetComp let) {
        var name = let.name;
        var expr = let.boundComp;
        var body = let.body;
        var newStack = new ArrayList<>(stack);
        newStack.add(let);
        return new Interpreter(expr, newStack, scope, false);
    }

    @Override
    public String toString() {
        return "Interpreter(" + term + ", " + terminated + ", " + scope.allBindings(Map.of()) + ")";
    }
}
