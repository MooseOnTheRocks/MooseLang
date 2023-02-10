package dev.foltz.mooselang.typing.value;

public class ValueUnit extends TypeValue {
    public ValueUnit() {}

    @Override
    public String toString() {
        return "Unit()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueUnit;
    }
}
