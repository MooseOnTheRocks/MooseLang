package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.RTValue;
import dev.foltz.mooselang.typing.types.Type;

public class Interpreter {
    private Env<Type> globalTypes;
    private Env<RTValue> globalValues;

    public Interpreter(Env<Type> globalTypes, Env<RTValue> globalValues) {
        this.globalTypes = globalTypes.copy();
        this.globalValues = globalValues.copy();
    }
}
