package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRDoComp extends IRComp {
    public final String name;
    public final IRComp boundComp;
    public final IRComp body;

    public IRDoComp(String name, IRComp boundComp, IRComp body) {
        this.name = name;
        this.boundComp = boundComp;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRDoComp(" + name + ", " + boundComp + ", " + body + ")";
    }
}
