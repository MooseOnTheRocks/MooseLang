package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.type.IRType;

public class StackEntryType extends StackEntry {
    public final IRType type;

    public StackEntryType(IRType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "StackEntryType(" + type + ")";
    }
}
