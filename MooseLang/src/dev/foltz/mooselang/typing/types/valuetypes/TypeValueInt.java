package dev.foltz.mooselang.typing.types.valuetypes;

public record TypeValueInt(int value) implements TypeValue {

    @Override
    public String toString() {
        return "" + value;
    }
}
