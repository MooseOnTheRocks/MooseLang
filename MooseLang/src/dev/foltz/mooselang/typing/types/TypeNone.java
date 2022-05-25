package dev.foltz.mooselang.typing.types;

public class TypeNone implements Type {
    public static final TypeNone INSTANCE = new TypeNone();

    private TypeNone() {}

    @Override
    public String toString() {
        return "None";
    }
}
