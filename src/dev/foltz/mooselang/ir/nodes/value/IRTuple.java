package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;

import java.util.List;

public class IRTuple extends IRValue {
    public final List<IRValue> values;

    public IRTuple(List<IRValue> values) {
        this.values = List.copyOf(values);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTuple(" + values + ")";
    }
}
