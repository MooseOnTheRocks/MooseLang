package dev.foltz.mooselang.interpreter.runtime;

import dev.foltz.mooselang.typing.types.Type;

public class RTType implements RTObject {
    public final Type type;

    public RTType(Type type) {
        this.type = type;
    }
}
