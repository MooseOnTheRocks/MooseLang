package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.IRName;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.typing.BaseType;
import dev.foltz.mooselang.typing.comp.CompType;
import dev.foltz.mooselang.typing.comp.Lambda;
import dev.foltz.mooselang.typing.comp.Producer;
import dev.foltz.mooselang.typing.comp.StackPush;
import dev.foltz.mooselang.typing.value.NumberType;
import dev.foltz.mooselang.typing.value.Thunk;
import dev.foltz.mooselang.typing.value.ValueType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypedIR extends VisitorIR<BaseType> {
    public final Map<String, ValueType> scope;

    public TypedIR(Map<String, ValueType> scope) {
        this.scope = Map.copyOf(scope);
    }

    private TypedIR put(String name, ValueType value) {
        var newScope = new HashMap<>(scope);
        newScope.put(name, value);
        return new TypedIR(newScope);
    }

    public Optional<ValueType> find(String name) {
        if (scope.containsKey(name)) {
            return Optional.of(scope.get(name));
        }
        else {
            return Optional.empty();
        }
    }

    public BaseType typeOf(IRNode node) {
        return node.apply(this);
    }

    @Override
    public BaseType visit(IRLambda lambda) {
        var bodyType = put(lambda.paramName, lambda.paramType).typeOf(lambda.body);
        if (bodyType instanceof CompType bodyComp) {
            return new Lambda(lambda.paramName, lambda.paramType, bodyComp);
        }
        else {
            return error("Lambda expects body of Computation, received: " + bodyType);
        }
    }

    @Override
    public BaseType visit(IRLetValue bind) {
        var exprType = typeOf(bind.value);
        if (exprType instanceof ValueType value) {
            var bodyType = put(bind.name, value).typeOf(bind.body);
            if (bodyType instanceof CompType comp) {
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
    public BaseType visit(IRLetComp bind) {
        var boundType = typeOf(bind.boundComp);
        if (boundType instanceof Producer producer) {
            var bodyType = put(bind.name, producer.value).typeOf(bind.body);
            if (bodyType instanceof CompType bodyComp) {
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
    public BaseType visit(IRForceName force) {
        var mbound = find(force.name);
        if (mbound.isEmpty()) {
            return error("Cannot find " + force.name + " in scope.");
        }
        var bound = mbound.get();
        if (bound instanceof Thunk thunk) {
            return thunk.comp;
        }
        else {
            return error("force expects Thunk, received: " + bound);
        }
    }

    @Override
    public BaseType visit(IRPush push) {
        var valueType = typeOf(push.value);
        if (valueType instanceof ValueType value) {
            var thenType = typeOf(push.then);
            if (thenType instanceof Lambda lambda) {
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
    public BaseType visit(IRThunk thunk) {
        var innerType = typeOf(thunk.comp);
        if (innerType instanceof CompType innerComp) {
            return new Thunk(innerComp);
        }
        else {
            return error("Thunk type expects Computation, received: " + innerType);
        }
    }

    @Override
    public BaseType visit(IRName name) {
        return find(name.name).orElseGet(() -> (ValueType) error("Cannot find " + name.name + " in scope."));
    }

    @Override
    public BaseType visit(IRNumber number) {
        return new NumberType();
    }

    private BaseType error(String msg) {
        throw new RuntimeException("[Typing Error] " + msg);
    }
}
