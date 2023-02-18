package dev.foltz.mooselang.ir.nodes.type;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRTypeName extends IRType {
    public final String name;

    public IRTypeName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeName(" + name + ")";
    }
}
