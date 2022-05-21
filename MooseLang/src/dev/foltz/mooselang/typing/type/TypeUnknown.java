package dev.foltz.mooselang.typing.type;

public class TypeUnknown implements Type {
    @Override
    public boolean isEqual(Type other) {
        return false;
    }

    @Override
    public String toString() {
        return "TypeUnknown()";
    }
}
