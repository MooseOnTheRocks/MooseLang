package dev.foltz.mooselang.typing.value;

import java.util.List;

public class ValueTuple extends TypeValue {
    public final List<TypeValue> values;

    public ValueTuple(List<TypeValue> values) {
        this.values = List.copyOf(values);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueTuple ot && ot.values.equals(values);
    }

    @Override
    public String toString() {
        return "ValueTuple(" + values + ")";
    }
}
