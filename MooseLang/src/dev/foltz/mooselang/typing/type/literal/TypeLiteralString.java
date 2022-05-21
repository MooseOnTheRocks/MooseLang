package dev.foltz.mooselang.typing.type.literal;

import dev.foltz.mooselang.typing.type.Type;

public class TypeLiteralString implements TypeLiteralValue {
    public final String value;

    public TypeLiteralString(String value) {
        this.value = value;
    }

    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeLiteralString ts && ts.value.equals(value);
    }

    @Override
    public String toString() {
        return "TypeLiteralString{" +
            "value='" + value + '\'' +
            '}';
    }
}
