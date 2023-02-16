package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.value.IRValue;

public class StackValue extends StackType {
    public final IRValue value;

    public StackValue(IRValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StackValue(" + value + ")";
    }
}
