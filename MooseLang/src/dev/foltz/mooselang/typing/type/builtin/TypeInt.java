package dev.foltz.mooselang.typing.type.builtin;

import dev.foltz.mooselang.typing.type.Type;

public class TypeInt implements Type {
    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeInt;
    }

    @Override
    public String toString() {
        return "TypeInt()";
    }
}
