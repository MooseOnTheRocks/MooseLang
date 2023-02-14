package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.TypeBase;
import dev.foltz.mooselang.typing.comp.TypeComp;
import dev.foltz.mooselang.typing.comp.CompLambda;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.value.*;

import java.util.*;

public class TypedIR extends VisitorIR<TypeBase> {
    public final Map<String, TypeValue> scope;

    public TypedIR(Map<String, TypeValue> scope) {
        this.scope = Map.copyOf(scope);
    }

    private TypedIR put(String name, TypeValue value) {
        var newScope = new HashMap<>(scope);
        newScope.put(name, value);
        return new TypedIR(newScope);
    }

    public Optional<TypeValue> find(String name) {
        if (scope.containsKey(name)) {
            return Optional.of(scope.get(name));
        }
        else {
            return Optional.empty();
        }
    }

    public TypeBase typeOf(IRNode node) {
        return node.apply(this);
    }

    @Override
    public TypeBase visit(IRBuiltin builtin) {
        return builtin.innerType;
    }

    @Override
    public TypeBase visit(IRProduce produce) {
        var valueType = typeOf(produce.value);
        if (valueType instanceof TypeValue value) {
            return new CompProducer(value);
        }
        else {
            return error("Produce expects Value, received: " + valueType);
        }
    }

    @Override
    public TypeBase visit(IRLambda lambda) {
        var bodyType = put(lambda.paramName, lambda.paramType).typeOf(lambda.body);
        if (bodyType instanceof TypeComp bodyComp) {
            return new CompLambda(lambda.paramName, lambda.paramType, bodyComp);
        }
        else {
            return error("Lambda expects body of Computation, received: " + bodyType);
        }
    }

    @Override
    public TypeBase visit(IRLet bind) {
        var exprType = typeOf(bind.value);
        if (exprType instanceof TypeValue value) {
            var bodyType = put(bind.name, value).typeOf(bind.body);
            if (bodyType instanceof TypeComp comp) {
                return comp;
            }
            else {
                return error("Let-value expected body of Computation, received: " + bodyType);
            }
        }
        else {
            return error("Let-value expected expression of Value, received: " + exprType);
        }
    }

    @Override
    public TypeBase visit(IRDo bind) {
        var boundType = typeOf(bind.boundComp);
        if (boundType instanceof CompProducer producer) {
            var bodyType = put(bind.name, producer.value).typeOf(bind.body);
            if (bodyType instanceof TypeComp bodyComp) {
                return bodyComp;
            }
            else {
                return error("Let-comp expected body of Computation, received: " + bodyType + "\nbind=" + bind);
            }
        }
        else {
            return error("Let-comp expected expression of Producer, received: " + boundType + "\nbind=" + bind);
        }
    }

    @Override
    public TypeBase visit(IRForce force) {
        if (force.thunk instanceof IRName name) {
            var mbound = find(name.name);
            if (mbound.isEmpty()) {
                return error("Cannot find " + name.name + " in scope.");
            }
            else if (mbound.get() instanceof ValueThunk thunk) {
                return thunk.comp;
            }
            else {
                return error("Force expects Thunk, received: " + mbound.get());
            }
        }
        else if (force.thunk instanceof IRThunk thunk) {
            return typeOf(thunk.comp);
        }
        else {
            return error("Force expects Thunk, received: " + force.thunk);
        }
    }

    @Override
    public TypeBase visit(IRPush push) {
        var valueType = typeOf(push.value);
        if (valueType instanceof TypeValue value) {
            var thenType = typeOf(push.then);
            if (thenType instanceof CompLambda lambda) {
                if (lambda.paramType.equals(value)) {
                    return lambda.bodyType;
                }
                else {
                    return error("Push expects lambda of " + valueType + ", received: " + lambda);
                }
            }
            else {
                return error("Push expects following lambda, received: " + thenType);
            }
        }
        else {
            return error("Push expects rhs of Value, received: " + valueType + "\nIRPush=" + push + "\nScope=" + scope);
        }
    }

    @Override
    public TypeBase visit(IRCaseOf caseOf) {
        var baseType = typeOf(caseOf.value);
        if (!(baseType instanceof TypeValue valueType)) {
            return error("case-of expects Value, received: " + baseType);
        }

        List<TypeBase> branchTypes = caseOf.branches.stream().map(b -> typeOfPattern(valueType, b.pattern, b.body)).toList();
        for (var type : branchTypes) {
            var expectedType = branchTypes.get(0);
            if (!type.equals(expectedType)) {
                return error("Expected branch type of " + expectedType + ", received: " + type);
            }
        }

        return branchTypes.get(0);

//        if (type instanceof ValueTuple tuple && caseOf.branches.size() == 1) {
//            IRCaseOfBranch branch = caseOf.branches.get(0);
//            boolean good = branch.pattern instanceof IRTuple pattern &&
//                pattern.values.size() == tuple.values.size() &&
//                pattern.values.stream().allMatch(v -> v instanceof IRName);
//            if (!(branch.pattern instanceof IRTuple pattern) || !good) {
//                return error("Bad branch pattern: " + branch.pattern);
//            }
//            var names = pattern.values.stream().map(v -> ((IRName) v).name).toList();
//            var typeState = this;
//            for (int i = 0; i < names.size(); i++) {
//                typeState = typeState.put(names.get(i), tuple.values.get(i));
//            }
//
//            var branchType = typeState.typeOf(branch.body);
//            if (branchType instanceof TypeComp) {
//                return branchType;
//            }
//            else {
//                return error("Expected branch of Computation, received: " + branchType);
//            }
//        }
//        else {
//            return error("Invalid IRCaseOf:\ntype: " + type + "\nbranches: " + caseOf.branches);
//        }
    }

    private TypeBase typeOfPattern(TypeValue valueType, IRValue pattern, IRComp body) {
        // This function does two things:
        //   - Ensure pattern can destruct value appropriately.
        //   - Return a typer with pattern in scope.

        if (pattern instanceof IRName patternName) {
            return put(patternName.name, valueType).typeOf(body);
        }
        else if (valueType instanceof ValueNumber typeNumber && pattern instanceof IRNumber patternNumber) {
            return typeOf(body);
        }
        else if (valueType instanceof ValueString typeString && pattern instanceof IRString patternString) {
            return typeOf(body);
        }
        else {
            return error("Cannot match type " + valueType + " with pattern: " + pattern);
        }
    }

    @Override
    public TypeBase visit(IRThunk thunk) {
        var innerType = typeOf(thunk.comp);
        if (innerType instanceof TypeComp innerComp) {
            return new ValueThunk(innerComp);
        }
        else {
            return error("Thunk type expects Computation, received: " + innerType);
        }
    }

    @Override
    public TypeBase visit(IRName name) {
        return find(name.name).orElseGet(() -> (TypeValue) error("Cannot find " + name.name + " in scope."));
    }

    @Override
    public TypeBase visit(IRNumber number) {
        return new ValueNumber();
    }

    @Override
    public TypeBase visit(IRString string) {
        return new ValueString();
    }

    @Override
    public TypeBase visit(IRUnit unit) {
        return new ValueUnit();
    }

    @Override
    public TypeBase visit(IRTuple tuple) {
        var valueTypes = tuple.values.stream().map(this::typeOf).toList();
        if (valueTypes.stream().anyMatch(t -> !(t instanceof TypeValue))) {
            return error("Tuple expects Value components, received: " + tuple.values);
        }
        return new ValueTuple(valueTypes.stream().map(t -> (TypeValue) t).toList());
    }

    private TypeBase error(String msg) {
        throw new RuntimeException("[Typing Error] " + msg);
    }
}
