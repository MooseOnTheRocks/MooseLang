package dev.foltz.mooselang.typing.value;

public class ValueString extends TypeValue {
    @Override
    public String toString() {
        return "String()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueString;
    }
}
