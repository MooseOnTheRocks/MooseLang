package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTFunc;

public class RTFuncRange extends RTFunc {
    public int from, to;

    public RTFuncRange() {
        super("range");
    }
}
