package dev.foltz.mooselang.typing.types;

public class TypeString implements Type {
    public static final TypeString INSTANCE = new TypeString();

    private TypeString() {}

    @Override
    public String toString() {
        return "String";
    }
}
