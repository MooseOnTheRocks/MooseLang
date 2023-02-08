package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRForceThunk extends IRComp {
    public final IRThunk thunk;

    public IRForceThunk(IRThunk thunk) {
        this.thunk = thunk;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRForce(" + thunk + ")";
    }
}
