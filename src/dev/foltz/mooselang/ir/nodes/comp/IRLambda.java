package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.typing.value.TypeValue;

public class IRLambda extends IRComp {
    public final String paramName;
    public final TypeValue paramType;
    public final IRComp body;

    public IRLambda(String paramName, TypeValue paramType, IRComp body) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.body = body;
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
