package dev.foltz.mooselang.typing.type;

import java.util.ArrayList;
import java.util.List;

public class TypeUnion implements Type {
    public final List<Type> types;

    public TypeUnion(List<Type> ts) {
        List<Type> allTypes = new ArrayList<>();
        for (Type t : ts) {
            if (t instanceof TypeUnion tu) {
                allTypes.addAll(tu.types);
            }
            else {
                allTypes.add(t);
            }
        }
        this.types = List.copyOf(allTypes);
    }

    @Override
    public boolean isEqual(Type other) {
        if (other instanceof TypeUnion tu) {
            var unmatchedTypes = new ArrayList<>(types);
            for (Type t : tu.types) {
                int index = unmatchedTypes.indexOf(t);
                if (index == -1) {
                    break;
                }
                unmatchedTypes.remove(index);
            }
            return unmatchedTypes.isEmpty();
        }
        return false;
    }

    @Override
    public String toString() {
        return "TypeUnion(" + String.join(", ", types.stream().map(Object::toString).toList()) + ")";
    }
}
