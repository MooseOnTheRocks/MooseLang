package dev.foltz.mooselang.ir.nodes;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class IRDefValue extends IRNode {
    public final String name;
    public final IRValue value;

    public IRDefValue(String name, IRValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRGlobalDef(" + name + ", " + value + ")";
    }
}
