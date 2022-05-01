package dev.foltz.mooselang.interpreter.rt;

import java.util.List;

public abstract class RTBuiltinFunc extends RTObject {
    public final String name;

    public RTBuiltinFunc(String name) {
        this.name = name;
    }

    public abstract RTObject call(List<RTObject> params);
}
