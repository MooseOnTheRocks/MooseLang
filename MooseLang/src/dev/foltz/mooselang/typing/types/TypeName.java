package dev.foltz.mooselang.typing.types;

public class TypeName implements Type {
    public final String name;

    public TypeName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
