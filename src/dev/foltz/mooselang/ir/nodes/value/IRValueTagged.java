package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.type.IRType;
import dev.foltz.mooselang.ir.nodes.type.IRTypeSum;
import dev.foltz.mooselang.ir.nodes.type.tag.IRTypeTag;

public class IRValueTagged extends IRValue {
    public final IRTypeTag tag;
    public final IRTypeSum type;

    public IRValueTagged(IRTypeTag tag, IRTypeSum type) {
        this.tag = tag;
        this.type = type;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRValueTagged(" + tag + ", " + type + ")";
    }
}
