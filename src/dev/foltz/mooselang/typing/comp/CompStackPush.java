package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.TypeValue;

public class CompStackPush extends TypeComp {
    public final TypeValue value;

    public CompStackPush(TypeValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StackPush(" + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CompStackPush push && push.value.equals(value);
    }
}
