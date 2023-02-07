package dev.foltz.mooselang.ir;

public class IRForceName extends IRComp {
    public final String name;

    public IRForceName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRForceName(" + name + ")";
    }
}
