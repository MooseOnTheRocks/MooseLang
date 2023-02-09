package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRDo extends IRComp {
    public final String name;
    public final IRComp boundComp;
    public final IRComp body;

    public IRDo(String name, IRComp boundComp, IRComp body) {
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
        return "IRDo(" + name + ", " + boundComp + ", " + body + ")";
    }
}
