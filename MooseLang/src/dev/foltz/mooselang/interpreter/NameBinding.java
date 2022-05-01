package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.rt.RTObject;

public record NameBinding(String name, RTObject boundObject) {
    public static final NameBinding NOT_PRESENT = new NameBinding(null, null);

    public boolean notPresent() {
        return this.equals(NOT_PRESENT);
    }
}
