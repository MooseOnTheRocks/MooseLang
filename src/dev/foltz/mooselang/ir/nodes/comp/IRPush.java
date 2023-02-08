package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.ir.VisitorIR;

public class IRPush extends IRComp {
    public final IRValue value;
    public final IRComp then;

    public IRPush(IRValue value, IRComp then) {
        this.value = value;
        this.then = then;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRPush(" + value + ", " + then + ")";
    }
}
