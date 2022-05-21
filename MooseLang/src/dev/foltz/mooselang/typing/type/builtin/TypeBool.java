package dev.foltz.mooselang.typing.type.builtin;

import dev.foltz.mooselang.typing.type.Type;

public class TypeBool implements Type {
    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeBool;
    }

    @Override
    public String toString() {
        return "TypeBool()";
    }
}
