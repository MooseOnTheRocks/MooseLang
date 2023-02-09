package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.value.IRName;
import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class IRForce extends IRComp {
    public final IRValue thunk;

    public IRForce(IRThunk thunk) {
        this.thunk = thunk;
    }

    public IRForce(IRName namedThunk) {
        this.thunk = namedThunk;
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
