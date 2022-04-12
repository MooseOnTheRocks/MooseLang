package dev.foltz.mooselang.interpreter.runtime;

public class RTNone extends RTObject {
    public static final RTNone INSTANCE = new RTNone();
    private RTNone() {
    }

    @Override
    public String toString() {
        return "RTNone{}";
    }
}
