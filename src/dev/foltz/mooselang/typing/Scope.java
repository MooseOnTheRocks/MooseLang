package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.types.value.ValueType;

import java.util.*;

public class Scope {
    public final Scope outerScope;
    public final Map<String, ValueType> localScope;

    public Scope(Scope outerScope) {
        this.outerScope = outerScope;
        localScope = outerScope == null ? Map.of() : outerScope.localScope;
    }

    private Scope(Scope outerScope, Map<String, ValueType> localScope) {
        this.outerScope = outerScope;
        this.localScope = localScope;
    }

    public Scope push() {
        return new Scope(this);
    }

    public Scope pop() {
        if (outerScope == null) {
            throw new RuntimeException("Cannot pop from outermost scope!");
        }
        return outerScope;
    }

    public Scope put(String name, ValueType value) {
        var newScope = new HashMap<>(localScope);
        newScope.put(name, value);
        return new Scope(outerScope, Map.copyOf(newScope));
    }

    public Optional<ValueType> find(String name) {
        if (localScope.containsKey(name)) {
            return Optional.of(localScope.get(name));
        }
        return outerScope == null ? Optional.empty() : outerScope.find(name);
    }

    public List<String> allNames(List<String> soFar) {
        var ls = new ArrayList<>(soFar);
        for (String name : localScope.keySet()) {
            if (!ls.contains(name)) {
                ls.add(name);
            }
        }
        soFar = List.copyOf(ls);

        return outerScope == null ? soFar : outerScope.allNames(soFar);
    }

    @Override
    public String toString() {
        return "Scope(" + allNames(List.of()) + ")";
    }
}
