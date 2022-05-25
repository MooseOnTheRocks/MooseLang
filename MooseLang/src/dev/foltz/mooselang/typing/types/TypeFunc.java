package dev.foltz.mooselang.typing.types;

import java.util.List;
import java.util.stream.Collectors;

public class TypeFunc implements Type {
    public final List<Type> paramTypes;
    public final Type retType;

    public TypeFunc(List<Type> paramTypes, Type retType) {
        this.paramTypes = List.copyOf(paramTypes);
        this.retType = retType;
    }

    @Override
    public String toString() {
        if (paramTypes.size() > 0) {
            return paramTypes.stream().map(Object::toString).collect(Collectors.joining(" -> ")) + " -> " + retType;
        }
        return "() -> " + retType;
    }
}
