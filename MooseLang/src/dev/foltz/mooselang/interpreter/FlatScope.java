package dev.foltz.mooselang.interpreter;

import java.util.ArrayList;
import java.util.List;

public class FlatScope<T> implements IScope<T> {
    public final List<NameBinding<T>> bindings;

    public FlatScope() {
        this.bindings = new ArrayList<>();
    }

    protected int indexOf(String name) {
        for (int i = 0; i < bindings.size(); i++) {
            NameBinding<T> binding = bindings.get(i);
            if (binding.name().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void bind(String name, T obj) {
        NameBinding<T> binding = new NameBinding<>(name, obj);
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
    public NameBinding<T> find(String name) {
        int index = indexOf(name);
        return index == -1 ? null : bindings.get(index);
    }
}
