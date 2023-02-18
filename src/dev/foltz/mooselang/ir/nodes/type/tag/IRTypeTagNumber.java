package dev.foltz.mooselang.ir.nodes.type.tag;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRTypeTagNumber extends IRTypeTag {
    public final double value;

    public IRTypeTagNumber(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IRTypeTagNumber number && number.value == value;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeTagNumber(" + value + ")";
    }
}
