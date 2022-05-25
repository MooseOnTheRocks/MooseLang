package dev.foltz.mooselang.typing.types;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeUnion implements Type {
    public final List<Type> types;

    public TypeUnion(List<Type> types) {
        this.types = List.copyOf(types);
    }

    @Override
    public String toString() {
        return types.stream().map(Objects::toString).collect(Collectors.joining(" | "));
    }
}
