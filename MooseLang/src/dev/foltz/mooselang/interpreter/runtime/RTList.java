package dev.foltz.mooselang.interpreter.runtime;

import java.util.List;

public class RTList extends RTObject {
    public final List<RTObject> elements;

    public RTList(List<RTObject> elements) {
        this.elements = List.copyOf(elements);
    }

    @Override
    public String toString() {
        return "RTList{" +
                "elements=" + elements +
                '}';
    }
}
