package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;

import java.util.*;
import java.util.stream.Collectors;

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
        if (value instanceof IRName name) {
            return resolve(find(name.name));
        }
        return value;
    }

    public Interpreter evaluate(IRComp comp) {
        return comp.apply(this);
    }

    @Override
    public Interpreter visit(IRBuiltin builtin) {
        return builtin.internal.apply(this);
    }

    private boolean patternMatches(IRValue value, IRValue pattern) {
        value = resolve(value);
        if (pattern instanceof IRName) {
            return true;
        }
        else if (value instanceof IRNumber valueNumber && pattern instanceof IRNumber patternNumber) {
            return valueNumber.value == patternNumber.value;
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

    private Interpreter bindPattern(IRValue value, IRValue pattern) {
        value = resolve(value);
        if (pattern instanceof IRName name) {
            return bind(name.name, value);
        }
        else if (value instanceof IRNumber valueNumber && pattern instanceof IRNumber patternNumber) {
            return this;
        }
        else if (value instanceof IRString valueString && pattern instanceof IRString patternString) {
            return this;
        }
        else if (value instanceof IRUnit && pattern instanceof IRUnit) {
            return this;
        }
        else {
            return error("Cannot bind destruct " + value + " into " + pattern);
        }
    }

    @Override
    public Interpreter visit(IRCaseOf caseOf) {
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
    public Interpreter visit(IRProduce produce) {
        var top = top();
        if (top instanceof StackFrame stackFrame) {
            return pop().bind(stackFrame.name, produce.value).withTerm(stackFrame.body);
        }
        else if (top == null) {
            return withTerm(new IRProduce(resolve(produce.value))).terminate();
        }
        return error("Produce expects null or StackFrame, received: " + top);
//        return withTerm(new IRProduce(resolve(produce.value))).terminate();
    }

    @Override
    public Interpreter visit(IRDo bind) {
        return pushFrame(new StackFrame(bind.name, bind.body)).withTerm(bind.boundComp);
//        var eval = evaluate(bind.boundComp);
//        if (eval.term instanceof IRProduce produce) {
//            return eval.bind(bind.name, produce.value).evaluate(bind.body);
//            return eval.bind(bind.name, produce.value).withTerm(bind.body);
//        }
//        return error("Expected IRProduce, received: " + eval.term);
//        return eval.term instanceof IRProduce produce
//            ? eval.bind(bind.name, produce.value).evaluate(bind.body)
//            : error("Expected IRProduce, received: " + eval.term);
    }

    @Override
    public Interpreter visit(IRLambda lambda) {
        var top = top();
        if (top instanceof StackValue stackValue) {
            return pop().bind(lambda.paramName, stackValue.value).withTerm(lambda.body);
        }
        return error("Lambda expects Value, received: " + top);
    }

    @Override
    public Interpreter visit(IRPush push) {
        return push(push.value).withTerm(push.then);
//        var eval = evaluate(push.then);
//        if (eval.term instanceof IRLambda lambda) {
//            return eval.bind(lambda.paramName, resolve(push.value)).withTerm(lambda.body);
//        }
//        return error("Expected IRLambda, received: " + eval.term);
//        return eval.term instanceof IRLambda lambda
//            ? eval.bind(lambda.paramName, push.value).evaluate(lambda.body)
//            : error("Expected IRLambda, received: " + eval.term);
    }

    @Override
    public Interpreter visit(IRLet bind) {
        return bind(bind.name, resolve(bind.value)).withTerm(bind.body);
    }

    @Override
    public Interpreter visit(IRForce force) {
        var mthunk = resolve(force.thunk);
        if (mthunk instanceof IRThunk thunk) {
            return withTerm(thunk.comp);
        }
        return error("Force expects Thunk, received: " + mthunk);
    }
}
