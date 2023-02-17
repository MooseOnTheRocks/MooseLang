package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRCompBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;

import java.util.*;

public class Interpreter extends VisitorIR<Interpreter> {
    public final IRComp term;
    public final Map<String, IRValue> context;
    public final List<StackType> stack;
    public final boolean terminated;

    public Interpreter(IRComp term, Map<String, IRValue> context, List<StackType> stack, boolean terminated) {
        this.term = term;
        this.context = Map.copyOf(context);
        this.stack = List.copyOf(stack);
        this.terminated = terminated;
    }

    public Interpreter terminate() {
        return new Interpreter(term, context, stack, true);
    }

    public Interpreter withTerm(IRComp newTerm) {
        return new Interpreter(newTerm, context, stack, terminated);
    }

    public Interpreter bind(String name, IRValue value) {
        var resolved = resolve(value);
//        System.out.println("bind " + name + " = " + value);
        var newContext = new HashMap<>(context);
        newContext.put(name, resolved);
        return new Interpreter(term, newContext, stack, terminated);
    }

    public Interpreter bindAll(Map<String, IRValue> bindings) {
        var interp = this;
        for (var binding : bindings.entrySet()) {
            interp = interp.bind(binding.getKey(), binding.getValue());
        }
        return interp;
    }

    public IRValue find(String name) {
        if (context.containsKey(name)) {
            return context.get(name);
        }
        throw new RuntimeException("[Interpreter Error] Cannot find " + name + " in scope.");
    }

    public Interpreter push(IRValue value) {
//        System.out.println("push " + value);
        var newStack = new ArrayList<>(stack);
        newStack.add(new StackValue(resolve(value)));
        return new Interpreter(term, context, newStack, terminated);
    }

    public Interpreter pushFrame(StackFrame frame) {
//        System.out.println("push " + frame);
        var newStack = new ArrayList<>(stack);
        newStack.add(frame);
        return new Interpreter(term, context, newStack, terminated);
    }

    public Interpreter pop() {
        if (stack.isEmpty()) {
            return error("Cannot pop empty stack.");
        }
//        System.out.println("pop " + top());
        var newStack = new ArrayList<>(stack);
        newStack.remove(newStack.size() - 1);
        return new Interpreter(term, context, newStack, terminated);
    }

    public StackType top() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.get(stack.size() - 1);
    }

    public Interpreter error(String msg) {
        throw new RuntimeException("[Interpreter Error] " + msg);
    }

    public Interpreter step() {
        return terminated ? this : evaluate(term);
    }

    public Interpreter stepAll() {
        var interp = this;
        while (!interp.terminated) {
//            System.out.println("Step!");
//            System.out.println("Term: " + interp.term);
//            System.out.println("Context: " + interp.context.entrySet().stream().filter(e -> !e.getKey().equals("-")).collect(Collectors.toMap(s -> s, item -> item)));
//            System.out.println("Stack: " + interp.stack);
//            System.out.println("Terminated: " + interp.terminated);
//            System.out.println("---");
            interp = interp.step();
        }
        return interp;
    }

    public IRValue resolve(IRValue value) {
        if (value instanceof IRValueName name) {
            return resolve(find(name.name));
        }
        return value;
    }

    public Interpreter evaluate(IRComp comp) {
        return comp.apply(this);
    }

    @Override
    public Interpreter visit(IRCompBuiltin builtin) {
        return builtin.internal.apply(this);
    }

    private boolean patternMatches(IRValue value, IRValue pattern) {
        value = resolve(value);
        if (pattern instanceof IRValueName) {
            return true;
        }
        else if (value instanceof IRValueNumber valueNumber && pattern instanceof IRValueNumber patternNumber) {
            return valueNumber.value == patternNumber.value;
        }
        else if (value instanceof IRValueString valueString && pattern instanceof IRValueString patternString) {
            return valueString.value.equals(patternString.value);
        }
        else if (value instanceof IRValueUnit && pattern instanceof IRValueUnit) {
            return true;
        }
        else if (value instanceof IRValueTuple valueTuple && pattern instanceof IRValueTuple patternTuple) {
            if (valueTuple.values.size() != patternTuple.values.size()) {
                return false;
            }
            for (int i = 0; i < valueTuple.values.size(); i++) {
                var valueElem = valueTuple.values.get(i);
                var patternElem = patternTuple.values.get(i);
                if (!patternMatches(valueElem, patternElem)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private Interpreter bindPattern(IRValue value, IRValue pattern) {
        value = resolve(value);
        if (pattern instanceof IRValueName name) {
            return bind(name.name, value);
        }
        else if (value instanceof IRValueNumber valueNumber && pattern instanceof IRValueNumber patternNumber) {
            return this;
        }
        else if (value instanceof IRValueString valueString && pattern instanceof IRValueString patternString) {
            return this;
        }
        else if (value instanceof IRValueUnit && pattern instanceof IRValueUnit) {
            return this;
        }
        else if (value instanceof IRValueTuple valueTuple && pattern instanceof IRValueTuple patternTuple) {
            if (valueTuple.values.size() != patternTuple.values.size()) {
                return this;
            }
            var interp = this;
            for (int i = 0; i < valueTuple.values.size(); i++) {
                var valueElem = valueTuple.values.get(i);
                var patternElem = patternTuple.values.get(i);
                if (!patternMatches(valueElem, patternElem)) {
                    return error("Cannot bind tuple " + value + " into " + pattern);
                }
                interp = interp.bindPattern(valueElem, patternElem);
            }
            return interp;
        }
        else {
            return error("Cannot bind destruct " + value + " into " + pattern);
        }
    }

    @Override
    public Interpreter visit(IRCompCaseOf caseOf) {
        // Find a matching branch, perform destructure-bindings, set term to matching body.
        // If no match (including no default branch), error.
        var value = resolve(caseOf.value);
        for (var branch : caseOf.branches) {
            var pattern = branch.pattern;
            if (patternMatches(value, pattern)) {
                return bindPattern(value, pattern).withTerm(branch.body);
            }
        }
        return error("Unhandled case: " + value);
    }

    @Override
    public Interpreter visit(IRCompProduce produce) {
        var value = produce.value;
        if (value instanceof IRValueThunk thunk) {
            var newClosure = new HashMap<>(thunk.closure);
            context.forEach((name, v) -> {
                if (!newClosure.containsKey(name)) {
//                    System.out.println("Adding to closure: " + name + " = " + v);
                }
                newClosure.putIfAbsent(name, v);
            });
            value = new IRValueThunk(thunk.comp, newClosure);
        }

        var top = top();
        if (top instanceof StackFrame stackFrame) {
            return pop().bind(stackFrame.name, value).withTerm(stackFrame.body);
        }
        else if (top == null) {
            return withTerm(new IRCompProduce(resolve(value))).terminate();
        }
        return error("Produce expects null or StackFrame, received: " + top);
    }

    @Override
    public Interpreter visit(IRCompDo bind) {
        return pushFrame(new StackFrame(bind.name, bind.body)).withTerm(bind.boundComp);
    }

    @Override
    public Interpreter visit(IRCompLambda lambda) {
        var top = top();
        if (top instanceof StackValue stackValue) {
            return pop().bind(lambda.paramName, stackValue.value).withTerm(lambda.body);
        }
        return error("Lambda expects Value, received: " + top);
    }

    @Override
    public Interpreter visit(IRCompPush push) {
        return push(push.value).withTerm(push.then);
    }

    @Override
    public Interpreter visit(IRCompLet bind) {
        return bind(bind.name, resolve(bind.value)).withTerm(bind.body);
    }

    @Override
    public Interpreter visit(IRCompForce force) {
        var mthunk = resolve(force.thunk);
        if (mthunk instanceof IRValueThunk thunk) {
            return bindAll(thunk.closure).withTerm(thunk.comp);
        }
        return error("Force expects Thunk, received: " + mthunk);
    }
}
