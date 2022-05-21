package dev.foltz.mooselang.typing.type.literal;

import dev.foltz.mooselang.typing.type.Type;

public class TypeLiteralInt implements TypeLiteralValue {
    public final int value;

    public TypeLiteralInt(int value) {
        this.value = value;
    }

    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeLiteralInt ti && ti.value == value;
    }

    @Override
    public String toString() {
        return "TypeLiteralInt{" +
            "value=" + value +
            '}';
    }
}
