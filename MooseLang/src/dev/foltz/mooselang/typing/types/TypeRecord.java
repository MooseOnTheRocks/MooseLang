package dev.foltz.mooselang.typing.types;

import java.util.Map;
import java.util.stream.Collectors;

public class TypeRecord implements Type {
    public final Map<String, Type> fields;

    public TypeRecord(Map<String, Type> fields) {
        this.fields = Map.copyOf(fields);
    }

    @Override
    public String toString() {
        return "{ " + fields.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", ")) + " }";
    }
}
