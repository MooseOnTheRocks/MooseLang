package dev.foltz.mooselang.typing.type.builtin;

import dev.foltz.mooselang.typing.type.Type;

public class TypeString implements Type {
    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeString;
    }

    @Override
    public String toString() {
        return "TypeString()";
    }
}
