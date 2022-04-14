package dev.foltz.mooselang.interpreter.runtime;

public class RTString extends RTObject {
    public String value;

    public RTString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RTString{" +
                "value='" + value + '\'' +
                '}';
    }
}
