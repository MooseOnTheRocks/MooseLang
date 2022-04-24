package dev.foltz.mooselang.interpreter.runtime;

import java.util.List;

public abstract class RTFunc extends RTObject {
    public abstract boolean accepts(List<RTObject> args);
}
