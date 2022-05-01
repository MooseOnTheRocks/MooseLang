package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.rt.RTObject;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

public class FlatScope implements IScope {
    public final List<NameBinding> bindings;

    public FlatScope(IScope scope) {
        this.bindings = new ArrayList<>(scope.bindings());
    }

    public FlatScope() {
        this.bindings = new ArrayList<>();
    }

    @Override
    public List<NameBinding> bindings() {
        return new ArrayList<>(bindings);
    }

    protected int indexOf(String name) {
        for (int i = 0; i < bindings.size(); i++) {
            NameBinding binding = bindings.get(i);
            if (binding.name().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void bind(String name, RTObject obj) {
        NameBinding binding = new NameBinding(name, obj);
        int index = indexOf(name);
        if (index == -1) {
            bindings.add(binding);
        }
        else {
            bindings.set(index, binding);
        }
    }

    @Override
    public boolean contains(String name) {
        return indexOf(name) != -1;
    }

    @Override
    public NameBinding find(String name) {
        int index = indexOf(name);
        return index == -1 ? null : bindings.get(index);
    }

    @Override
    public String toString() {
        return "FlatScope{" +
                "bindings=" + bindings +
                '}';
    }
}
