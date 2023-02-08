package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRName extends IRValue {
    public final String name;

    public IRName(String name) {
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
