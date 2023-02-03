package dev.foltz.mooselang.ir.types.comp;

import dev.foltz.mooselang.ir.types.value.ValueType;

public class Producer extends CompType {
    public final ValueType value;

    public Producer(ValueType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Producer(" + value + ")";
    }
}
