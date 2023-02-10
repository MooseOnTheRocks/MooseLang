package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.TypeValue;

public class CompProducer extends TypeComp {
    public final TypeValue value;

    public CompProducer(TypeValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Producer(" + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CompProducer prod && prod.value.equals(value);
    }
}
