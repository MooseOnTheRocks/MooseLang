package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRValueName extends IRValue {
    public final String name;

    public IRValueName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRName(" + name + ")";
    }
}
