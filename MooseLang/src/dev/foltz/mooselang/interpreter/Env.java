package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.RTFuncDef;
import dev.foltz.mooselang.interpreter.runtime.RTObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Env {
    private final List<Map<String, RTObject>> scopedBindings;

    public Env() {
        scopedBindings = new ArrayList<>();
        scopedBindings.add(new LinkedHashMap<>());
    }

    public void pushScope() {
        scopedBindings.add(new LinkedHashMap<>());
    }

    public void popScope() {
        scopedBindings.remove(scopedBindings.size() - 1);
    }

    public RTObject find(String name) {
        for (Map<String, RTObject> scope : scopedBindings) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    public void bind(String name, RTObject object) {
        RTObject binding = find(name);
        if (binding != null && !(binding instanceof RTFuncDef)) {
            throw new IllegalStateException("Multiple bindings for: " + name);
        }
        Map<String, RTObject> scope = scopedBindings.get(scopedBindings.size() - 1);
        scope.put(name, object);
    }

    @Override
    public String toString() {
        return "Env{" +
                "scopedBindings=" + scopedBindings +
                '}';
    }
}
