package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.ir.VisitorIR;

import java.util.Map;

public class IRThunk extends IRValue {
    public final IRComp comp;
    public final Map<String, IRValue> closure;

    public IRThunk(IRComp comp, Map<String, IRValue> closure) {
        this.comp = comp;
        this.closure = Map.copyOf(closure);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRThunk(" + comp + ", " + closure + ")";
    }
}
