package dev.foltz.mooselang.ir;

public class IRLetValue extends IRComp {
    public final String name;
    public final IRValue value;
    public final IRComp body;

    public IRLetValue(String name, IRValue value, IRComp body) {
        this.name = name;
        this.value = value;
        this.body = body;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRLetValue(" + name + ", " + value + ", " + body + ")";
    }
}
