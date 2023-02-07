package dev.foltz.mooselang.typing.value;

public class StringType extends ValueType {
    @Override
    public String toString() {
        return "String()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof StringType;
    }
}
