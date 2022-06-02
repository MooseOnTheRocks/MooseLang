package dev.foltz.mooselang.typing.types.valuetypes;

public record TypeValueString(String value) implements TypeValue {

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
