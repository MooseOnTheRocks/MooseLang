package dev.foltz.mooselang.typing.types.valuetypes;

public record TypeValueBool(boolean value) implements TypeValue {
    @Override
    public String toString() {
        return "" + value;
    }
}
