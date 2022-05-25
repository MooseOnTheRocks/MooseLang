package dev.foltz.mooselang.typing.types;

public class AnyType implements Type {
    public final static AnyType INSTANCE = new AnyType();

    private AnyType() {}

    @Override
    public String toString() {
        return "Any";
    }
}
