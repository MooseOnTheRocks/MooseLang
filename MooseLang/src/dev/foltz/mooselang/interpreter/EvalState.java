package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.RTValue;
import dev.foltz.mooselang.typing.types.Type;

public class EvalState {
    public final Env<Type> typeNames;
    public final Env<RTValue> valueNames;
    public final RTValue value;

    public EvalState(Env<Type> typeNames, Env<RTValue> valueNames, RTValue value) {
        this.typeNames = typeNames.copy();
        this.valueNames = valueNames.copy();
        this.value = value;
    }

    @Override
    public String toString() {
        return "EvalState{" +
            "\ntypeNames=" + typeNames +
            "\n, valueNames=" + valueNames +
            "\n, value=" + value +
            '}';
    }
}
