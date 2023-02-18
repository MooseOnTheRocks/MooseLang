package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class StackEntryValue extends StackEntry {
    public final IRValue value;

    public StackEntryValue(IRValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StackValue(" + value + ")";
    }
}
