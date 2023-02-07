package dev.foltz.mooselang.ir;

public class IRThunk extends IRValue {
    public final IRComp comp;

    public IRThunk(IRComp comp) {
        this.comp = comp;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRThunk(" + comp + ")";
    }
}
