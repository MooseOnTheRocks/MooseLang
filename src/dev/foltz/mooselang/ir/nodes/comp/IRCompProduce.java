package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.ir.VisitorIR;

public class IRCompProduce extends IRComp {
    public final IRValue value;

    public IRCompProduce(IRValue value) {
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRProduce(" + value + ")";
    }
}
