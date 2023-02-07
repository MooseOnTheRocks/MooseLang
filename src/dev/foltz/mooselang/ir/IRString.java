package dev.foltz.mooselang.ir;

public class IRString extends IRValue {
    public final String value;

    public IRString(String value) {
        this.value = value;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRString(\"" + value + "\")";
    }
}
