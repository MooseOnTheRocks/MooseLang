package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.typing.value.TypeValue;

public class IRCompLambda extends IRComp {
    public final String paramName;
    public final TypeValue paramType;
    public final IRComp body;
//    public final Map<String, IRValue> closure;

    public IRCompLambda(String paramName, TypeValue paramType, IRComp body) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.body = body;
//        this.closure = Map.copyOf(closure);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRLambda(" + paramName + ", " + paramType + ", " + body + ")";
    }
}
