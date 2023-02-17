package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class IRCompForce extends IRComp {
    public final IRValue thunk;

    public IRCompForce(IRValue thunk) {
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
