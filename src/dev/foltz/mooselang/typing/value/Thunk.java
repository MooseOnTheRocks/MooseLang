package dev.foltz.mooselang.typing.value;

import dev.foltz.mooselang.typing.comp.CompType;

public class Thunk extends ValueType {
    public final CompType comp;

    public Thunk(CompType type) {
        this.comp = type;
    }

    @Override
    public String toString() {
        return "Thunk(" + comp + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Thunk thunk && thunk.comp.equals(comp);
    }
}
