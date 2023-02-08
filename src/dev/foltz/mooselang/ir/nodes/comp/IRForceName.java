package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRForceName extends IRComp {
    public final String name;

    public IRForceName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRForceName(" + name + ")";
    }
}
