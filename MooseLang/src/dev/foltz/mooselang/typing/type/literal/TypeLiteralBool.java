package dev.foltz.mooselang.typing.type.literal;

import dev.foltz.mooselang.typing.type.Type;

public class TypeLiteralBool implements TypeLiteralValue {
    public final boolean value;

    public TypeLiteralBool(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeLiteralBool tb && tb.value == value;
    }

    @Override
    public String toString() {
        return "TypeLiteralBool{" +
            "value=" + value +
            '}';
    }
}
