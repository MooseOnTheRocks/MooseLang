package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.ValueType;

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