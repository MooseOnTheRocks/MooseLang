package dev.foltz.mooselang.ir.nodes.type;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRTypeUnit extends IRType {
    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeUnit()";
    }
}
