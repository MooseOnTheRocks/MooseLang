package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.value.IRValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ScopeOld {
    public final ScopeOld prev;
    public final ScopeOld outer;
    public final Map<String, IRValue> localScope;

    public ScopeOld(ScopeOld prev, ScopeOld outer, Map<String, IRValue> localScope) {
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

    public ScopeOld put(String name, IRValue value) {
        return new ScopeOld(this, outer, Map.of(name, value));
    }

    public ScopeOld push() {
        return new ScopeOld(this, this, Map.of());
    }

    public ScopeOld pop() {
        return outer;
    }

    @Override
    public String toString() {
        return "Scope(" + localScope + ")";
    }
}
