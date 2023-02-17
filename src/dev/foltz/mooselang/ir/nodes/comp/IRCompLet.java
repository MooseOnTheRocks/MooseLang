package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.ir.VisitorIR;

public class IRCompLet extends IRComp {
    public final String name;
    public final IRValue value;
    public final IRComp body;

    public IRCompLet(String name, IRValue value, IRComp body) {
        this.name = name;
        this.value = value;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRLetValue(" + name + ", " + value + ", " + body + ")";
    }
}
