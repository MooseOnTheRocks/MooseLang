package dev.foltz.mooselang.typing.types;

public class TypeInt implements Type {
    public static final TypeInt INSTANCE = new TypeInt();

    private TypeInt() {}

    @Override
    public String toString() {
        return "Int";
    }
}
