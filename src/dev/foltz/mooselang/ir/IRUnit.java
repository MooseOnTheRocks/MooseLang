package dev.foltz.mooselang.ir;

public class IRUnit extends IRValue {
    public IRUnit() {
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRUnit()";
    }
}
