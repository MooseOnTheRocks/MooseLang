package dev.foltz.mooselang.typing.types;

public class NoType implements Type {
    public static final NoType INSTANCE = new NoType();

    private NoType() {
    }

    @Override
    public String toString() {
        return "NoType";
    }
}
