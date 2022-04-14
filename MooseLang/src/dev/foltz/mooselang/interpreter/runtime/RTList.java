package dev.foltz.mooselang.interpreter.runtime;

import java.util.ArrayList;
import java.util.List;

public class RTList extends RTObject {
    public List<RTObject> elems;

    public RTList(List<RTObject> elems) {
        this.elems = new ArrayList<>(elems);
    }

    @Override
    public String toString() {
        return "RTList{" +
                "elems=" + elems +
                '}';
    }
}
