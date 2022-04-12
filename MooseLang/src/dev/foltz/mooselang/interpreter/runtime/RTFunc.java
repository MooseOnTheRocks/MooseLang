package dev.foltz.mooselang.interpreter.runtime;

import java.util.List;

public abstract class RTFunc extends RTObject {
    public final List<String> paramNames;

    public RTFunc(List<String> paramNames) {
        this.paramNames = List.copyOf(paramNames);
    }

    public abstract RTObject call(List<RTObject> params);

    @Override
    public String toString() {
        return "RTFunc{}";
    }
}
