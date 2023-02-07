package dev.foltz.mooselang.ir;

public class IRLetComp extends IRComp {
    public final String name;
    public final IRComp boundComp;
    public final IRComp body;

    public IRLetComp(String name, IRComp boundComp, IRComp body) {
        this.name = name;
        this.boundComp = boundComp;
        this.body = body;
    }

    @Override
    public <T> T apply(IRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRLetComp(" + name + ", " + boundComp + ", " + body + ")";
    }
}
