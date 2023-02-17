package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRValueString extends IRValue {
    public final String value;

    public IRValueString(String value) {
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRString(\"" + value + "\")";
    }
}
