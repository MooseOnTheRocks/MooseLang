package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.value.IRValue;

import java.util.List;

public class IRCompCaseOf extends IRComp {
    public final IRValue value;
    public final List<IRCompCaseOfBranch> branches;

    public IRCompCaseOf(IRValue value, List<IRCompCaseOfBranch> branches) {
        this.value = value;
        this.branches = branches;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRCaseOf(" + value + ", " + branches + ")";
    }
}
