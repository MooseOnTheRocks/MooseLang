package dev.foltz.mooselang.typing.value;

public class NumberType extends ValueType {
    public NumberType() {
    }

    @Override
    public String toString() {
        return "Number()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NumberType;
    }
}
