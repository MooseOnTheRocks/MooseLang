package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class IRCaseOfBranch extends IRComp {
    public final IRValue pattern;

    public final IRComp body;

    public IRCaseOfBranch(IRValue pattern, IRComp body) {
        this.pattern = pattern;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRCaseOfBranch(" + pattern + ", " + body + ")";
    }
}
