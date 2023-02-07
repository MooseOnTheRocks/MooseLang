package dev.foltz.mooselang.ir;

public class IRNumber extends IRValue {
    public final double value;

    public IRNumber(double value) {
        this.value = value;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRNumber(" + value + ")";
    }
}
