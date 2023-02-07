package dev.foltz.mooselang.ir;

public class IRName extends IRValue {
    public final String name;

    public IRName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRName(" + name + ")";
    }
}
