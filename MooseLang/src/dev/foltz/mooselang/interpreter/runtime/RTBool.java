package dev.foltz.mooselang.interpreter.runtime;

public class RTBool extends RTObject {
    public boolean value;

    public RTBool(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RTBool{" +
                "value=" + value +
                '}';
    }
}
