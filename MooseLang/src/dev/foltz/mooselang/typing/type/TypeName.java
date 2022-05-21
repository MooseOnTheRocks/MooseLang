package dev.foltz.mooselang.typing.type;

public class TypeName implements Type {
    public final String name;

    public TypeName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEqual(Type other) {
        return other instanceof TypeName tn && tn.name.equals(name);
    }

    @Override
    public String toString() {
        return "TypeName{" +
                "name='" + name + '\'' +
                '}';
    }
}
