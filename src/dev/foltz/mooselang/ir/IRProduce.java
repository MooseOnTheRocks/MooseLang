package dev.foltz.mooselang.ir;

public class IRProduce extends IRComp {
    public final IRValue value;

    public IRProduce(IRValue value) {
        this.value = value;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRProduce(" + value + ")";
    }
}
