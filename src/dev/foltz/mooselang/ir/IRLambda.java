package dev.foltz.mooselang.ir;

public class IRLambda extends IRComp {
    public final String paramName;
    public final IRComp body;

    public IRLambda(String paramName, IRComp body) {
        this.paramName = paramName;
        this.body = body;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRLambda(" + paramName + ", " + body + ")";
    }
}
