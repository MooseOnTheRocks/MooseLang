package dev.foltz.mooselang.typing.value;

public class Unit extends ValueType {
    public Unit() {}

    @Override
    public String toString() {
        return "Unit()";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Unit;
    }
}
