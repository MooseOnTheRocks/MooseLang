package dev.foltz.mooselang.ir.nodes.types;

import dev.foltz.mooselang.ir.VisitorIR;

import java.util.List;

public class IRTypeTuple extends IRType {
    public final List<IRType> types;

    public IRTypeTuple(List<IRType> types) {
        this.types = List.copyOf(types);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeTuple(" + types + ")";
    }
}
