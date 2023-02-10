package dev.foltz.mooselang.typing.value;

public class ValueNumber extends TypeValue {
    public ValueNumber() {
    }

    @Override
    public String toString() {
        return "Number()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueNumber;
    }
}
