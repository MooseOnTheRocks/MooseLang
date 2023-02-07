package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.ValueType;

public class StackPush extends CompType {
    public final ValueType value;

    public StackPush(ValueType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StackPush(" + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StackPush push && push.value.equals(value);
    }
}
