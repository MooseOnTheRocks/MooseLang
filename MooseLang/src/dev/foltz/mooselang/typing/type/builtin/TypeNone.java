package dev.foltz.mooselang.typing.type.builtin;

import dev.foltz.mooselang.typing.type.Type;

public class TypeNone implements Type {
    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeNone;
    }

    @Override
    public String toString() {
        return "TypeNone()";
    }
}
