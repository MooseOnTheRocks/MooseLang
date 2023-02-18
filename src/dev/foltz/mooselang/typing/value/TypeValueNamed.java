package dev.foltz.mooselang.typing.value;

public class TypeValueNamed extends TypeValue {
    public final String name;

    public TypeValueNamed(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object other) {
        return other instanceof TypeValueNamed named && named.name.equals(name);
    }

    @Override
    public String toString() {
        return "TypeValueName(" + name + ")";
    }
}
