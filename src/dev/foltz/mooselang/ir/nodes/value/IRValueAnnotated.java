package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.type.IRType;

public class IRValueAnnotated extends IRValue {
    public final IRValue value;
    public final IRType type;

    public IRValueAnnotated(IRValue value, IRType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRValueAnnotated(" + value + ", " + type + ")";
    }
}
