package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRCompBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.type.IRType;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.TypeBase;
import dev.foltz.mooselang.typing.value.*;

import java.util.*;

public class Interpreter extends VisitorIR<Interpreter> {
    public final IRComp term;
    public final Map<String, IRValue> context;
    public final Map<String, IRType> definedTypes;
    public final List<StackEntry> stack;
    public final boolean terminated;

    public Interpreter(IRComp term, Map<String, IRValue> context, List<StackEntry> stack, boolean terminated, Map<String, IRType> definedTypes) {
        this.term = term;
        this.context = Map.copyOf(context);
        this.stack = List.copyOf(stack);
        this.terminated = terminated;
        this.definedTypes = Map.copyOf(definedTypes);
    }

    public Interpreter terminate() {
        return new Interpreter(term, context, stack, true, Map.of());
    }

    public Interpreter withTerm(IRComp newTerm) {
        return new Interpreter(newTerm, context, stack, terminated, Map.of());
    }

    public Interpreter bind(String name, IRValue value) {
        var resolved = resolve(value);
//        System.out.println("bind " + name + " = " + value);
        var newContext = new HashMap<>(context);
        newContext.put(name, resolved);
        return new Interpreter(term, newContext, stack, terminated, Map.of());
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

    public IRType findType(String name) {
        if (definedTypes.containsKey(name)) {
            return definedTypes.get(name);
        }
        throw new RuntimeException("[Interpreter Error] Cannot find type " + name + " in scope.");
    }

    public Interpreter push(IRValue value) {
//        System.out.println("push " + value);
        var newStack = new ArrayList<>(stack);
        newStack.add(new StackEntryValue(resolve(value)));
        return new Interpreter(term, context, newStack, terminated, Map.of());
    }

    public Interpreter pushFrame(StackEntryFrame frame) {
//        System.out.println("push " + frame);
        var newStack = new ArrayList<>(stack);
        newStack.add(frame);
        return new Interpreter(term, context, newStack, terminated, Map.of());
    }

    public Interpreter pop() {
        if (stack.isEmpty()) {
            return error("Cannot pop empty stack.");
        }
//        System.out.println("pop " + top());
        var newStack = new ArrayList<>(stack);
        newStack.remove(newStack.size() - 1);
        return new Interpreter(term, context, newStack, terminated, Map.of());
    }

    public StackEntry top() {
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
//        System.out.println("VALUE = " + value);
        value = unwrap(resolve(value));
        pattern = unwrap(pattern);
//        System.out.println("Unwrapped pattern: " + pattern);
//        System.out.println("Resolved pattern: " + resolve(pattern));
//        System.out.println("VALUE UNWRAPPED = " + value);
        if (pattern instanceof IRValueName name) {
            if (Character.isUpperCase(name.name.charAt(0))) {
                return patternMatches(value, resolve(pattern));
            }
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
        else if (value instanceof IRValueTagged valueTagged && pattern instanceof IRValueTagged patternTagged) {
            return valueTagged.type.equals(patternTagged.type) && valueTagged.tag.equals(patternTagged.tag);
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
        value = unwrap(resolve(value));
        pattern = unwrap(pattern);
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
        else if (value instanceof IRValueTagged valueTagged && pattern instanceof IRValueTagged patternTagged) {
            if (valueTagged.type.equals(patternTagged.type) && valueTagged.tag.equals(patternTagged.tag)) {
                return this;
            }
            else {
                return error("Cannot match: " + valueTagged + " with " + patternTagged);
            }
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
        var value = unwrap(resolve(caseOf.value));
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
        if (top instanceof StackEntryFrame stackFrame) {
            return pop().bind(stackFrame.name, value).withTerm(stackFrame.body);
        }
        else if (top == null) {
            return withTerm(new IRCompProduce(resolve(value))).terminate();
        }
        return error("Produce expects null or StackFrame, received: " + top);
    }

    @Override
    public Interpreter visit(IRCompDo bind) {
        return pushFrame(new StackEntryFrame(bind.name, bind.body)).withTerm(bind.boundComp);
    }

    @Override
    public Interpreter visit(IRCompLambda lambda) {
        var top = top();
        if (top instanceof StackEntryValue stackValue) {
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

    public boolean typesMatch(IRValue a, TypeBase b) {
        // TODO: Fix IRValueFunctionHandle to combine the lambda parameter paths.
        // I.e., with multiple parameters, if e.g. the first two parameters have the same type,
        // then the entry for that function name should be another IRValueFunctionHandle.
        // This way, force-chains will unroll appropriately based on applied values.
        // Type checking before-hand should eliminate ambiguous paths.
        a = resolve(a);
        if (a instanceof IRValueAnnotated annotated) {
            return typesMatch(annotated.value, b);
        }
        else if (a instanceof IRValueNumber && b instanceof ValueNumber) {
            return true;
        }
        else if (a instanceof IRValueString && b instanceof ValueString) {
            return true;
        }
        else if (a instanceof IRValueUnit && b instanceof ValueUnit) {
            return true;
        }
        else if (a instanceof IRValueTagged tagged && b instanceof TypeValueNamed named) {
            return tagged.type.typeName.equals(named.name);
        }
        else if (a instanceof IRValueTuple at && b instanceof ValueTuple bt) {
            if (at.values.size() != bt.values.size()) {
                return false;
            }
            for (int i = 0; i < at.values.size(); i++) {
                if (!typesMatch(at.values.get(i), bt.values.get(i))) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private IRValue unwrap(IRValue value) {
        if (value instanceof IRValueTuple tuple) {
            return new IRValueTuple(tuple.values.stream().map(this::unwrap).toList());
        }
        else if (value instanceof IRValueAnnotated annotated) {
            return unwrap(annotated.value);
        }
        return value;
//        return value instanceof IRValueAnnotated annotated ? unwrap(annotated.value) : value;
    }

    @Override
    public Interpreter visit(IRCompForce force) {
        var mthunk = resolve(force.thunk);
        if (mthunk instanceof IRValueThunk thunk) {
            return bindAll(thunk.closure).withTerm(thunk.comp);
        }
        else if (mthunk instanceof IRValueFunctionHandle handle) {
            if (top() instanceof StackEntryValue entry) {
                var value = unwrap(entry.value);
//                System.out.println("Top = " + value);
                // Select (at-runtime) appropriate definition.
                // TODO: Make IRGlobalDef hold more utility information about itself.
                for (var def : handle.defs) {
//                    System.out.println("Looking at def: " + def);
                    if (def.value instanceof IRValueThunk defThunk && defThunk.comp instanceof IRCompLambda defLambda) {
                        var paramType = defLambda.paramType;
//                        System.out.println("Matching: " + value + " with " + paramType);
                        if (typesMatch(value, paramType)) {
                            return bindAll(defThunk.closure).withTerm(defLambda);
                        }
                    }
                }
                return error("Unable to dispatch function " + handle.name + " on " + mthunk + " top = " + entry);
            }
        }
        return error("Force expects Thunk, received: " + mthunk);
    }
}
