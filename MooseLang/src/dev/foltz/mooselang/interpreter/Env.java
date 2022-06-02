package dev.foltz.mooselang.interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Env<T> {
    protected final Map<String, T> mapping;

    public Env(Env<T> other) {
        mapping = new HashMap<>(other.mapping);
    }

    public Env(Map<String, T> mapping) {
        this.mapping = new HashMap<>(mapping);
    }

    public Env() {
        mapping = new HashMap<>();
    }

    public Optional<T> find(String name) {
        return Optional.ofNullable(mapping.get(name));
    }

    public boolean bind(String name, T t) {
        if (mapping.containsKey(name)) {
            return false;
        }
        mapping.put(name, t);
        return true;
    }

    public Env<T> with(String name, T value) {
        if (contains(name)) {
            String msg = "Env.with: cannot bind already bound name in scope: " + name + " = " + value + "\n"
                + "Previously bound: " + name + " = " + find(name).get();
            throw new IllegalStateException(msg);
        }

        var newEnv = copy();
        newEnv.bind(name, value);
        return newEnv;
    }

    public boolean unbind(String name) {
        if (!mapping.containsKey(name)) {
            return false;
        }
        mapping.remove(name);
        return true;
    }

    public Env<T> copy() {
        return new Env<>(this);
    }

    public boolean contains(String name) {
        return find(name).isPresent();
    }
}
