package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.IRValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Scope {
    public final Scope prev;
    public final Scope outer;
    public final Map<String, IRValue> localScope;

    public Scope(Scope prev, Scope outer, Map<String, IRValue> localScope) {
        this.prev = prev;
        this.outer = outer;
        this.localScope = Map.copyOf(localScope);
    }

    public Map<String, IRValue> allBindings(Map<String, IRValue> soFar) {
        var nextMap = new HashMap<>(soFar);
        for (var entry : localScope.entrySet()) {
            if (!soFar.containsKey(entry.getKey())) {
                nextMap.put(entry.getKey(), entry.getValue());
            }
        }
        return Map.copyOf(nextMap);
    }

    public Optional<IRValue> find(String name) {
        if (localScope.containsKey(name)) {
            return Optional.of(localScope.get(name));
        }
        return prev == null ? Optional.empty() : prev.find(name);
    }

    public Scope put(String name, IRValue value) {
        return new Scope(this, outer, Map.of(name, value));
    }

    public Scope push() {
        return new Scope(this, this, Map.of());
    }

    public Scope pop() {
        return outer;
    }

    @Override
    public String toString() {
        return "Scope(" + localScope + ")";
    }
}
