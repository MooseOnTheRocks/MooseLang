package dev.foltz.mooselang.typing.types;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record TypeTuple(List<Type> types) implements Type {
    public TypeTuple(List<Type> types) {
        this.types = List.copyOf(types);
    }

    @Override
    public String toString() {
        return "(" + types.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }
}
