package dev.foltz.mooselang.ir;

public class IRForceThunk extends IRComp {
    public final IRThunk thunk;

    public IRForceThunk(IRThunk thunk) {
        this.thunk = thunk;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRForce(" + thunk + ")";
    }
}
