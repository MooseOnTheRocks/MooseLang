package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.RTObject;

import java.util.*;

public class Env {
    private final List<List<Binding>> scopedBindings;

    public Env() {
        scopedBindings = new ArrayList<>();
        scopedBindings.add(new ArrayList<>());
    }

    public void pushScope() {
        scopedBindings.add(0, new ArrayList<>());
    }

    public void popScope() {
        scopedBindings.remove(0);
    }

    public int findIndex(String name) {
        for (int i = 0; i < scopedBindings.size(); i++) {
            List<Binding> scope = scopedBindings.get(i);
            for (Binding binding : scope) {
                if (binding.name.equals(name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public RTObject findInScope(String name) {
        for (Binding binding : scopedBindings.get(0)) {
            if (binding.name.equals(name)) {
                return binding.object;
            }
        }
        return null;
    }

    public RTObject find(String name) {
        for (List<Binding> scope : scopedBindings) {
            for (Binding binding : scope) {
                if (binding.name.equals(name)) {
                    return binding.object;
                }
            }
        }
        return null;
    }

    public void reassign(String name, RTObject object) {
        int index = findIndex(name);
        if (index == -1) {
            throw new IllegalStateException("Cannot reassign undefined object: " + object);
        }
        List<Binding> scope = scopedBindings.get(index);
        for (Binding binding : scope) {
            if (binding.name.equals(name)) {
                binding.object = object;
            }
        }
    }

    public void bind(String name, RTObject object) {
        RTObject binding = find(name);
        List<Binding> scope = scopedBindings.get(0);
        scope.add(0, new Binding(name, object));
    }

    @Override
    public String toString() {
        return "Env{" +
                "scopedBindings=" + scopedBindings +
                '}';
    }

    public static class Binding {
        public String name;
        public RTObject object;

        public Binding(String name, RTObject object) {
            this.name = name;
            this.object = object;
        }
    }
}
