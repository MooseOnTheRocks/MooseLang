package dev.foltz.mooselang.typing.value;

import dev.foltz.mooselang.typing.comp.TypeComp;

public class ValueThunk extends TypeValue {
    public final TypeComp comp;

    public ValueThunk(TypeComp type) {
        this.comp = type;
    }

    @Override
    public String toString() {
        return "Thunk(" + comp + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValueThunk thunk && thunk.comp.equals(comp);
    }
}
