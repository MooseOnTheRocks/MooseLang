package dev.foltz.mooselang.interpreter.rt;

import dev.foltz.mooselang.interpreter.RTVisitor;

public class RTInt extends RTObject {
    public final int value;

    public RTInt(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(RTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
