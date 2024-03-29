package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRValueNumber extends IRValue {
    public final double value;

    public IRValueNumber(double value) {
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRNumber(" + value + ")";
    }
}
