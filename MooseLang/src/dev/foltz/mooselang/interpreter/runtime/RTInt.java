package dev.foltz.mooselang.interpreter.runtime;

public class RTInt extends RTObject {
    public final int value;

    public RTInt(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RTInt{" +
                "value=" + value +
                '}';
    }
}
