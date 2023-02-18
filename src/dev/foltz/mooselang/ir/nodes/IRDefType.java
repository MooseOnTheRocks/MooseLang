package dev.foltz.mooselang.ir.nodes;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.type.IRType;

public class IRDefType extends IRNode {
    public final String name;
    public final IRType type;

    public IRDefType(String name, IRType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRDefType(" + name + ", " + type + ")";
    }
}
