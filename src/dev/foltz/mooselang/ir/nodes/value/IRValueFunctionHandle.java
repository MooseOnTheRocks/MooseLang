package dev.foltz.mooselang.ir.nodes.value;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.IRDefValue;

import java.util.List;

public class IRValueFunctionHandle extends IRValue {
    public final String name;
    public final List<IRDefValue> defs;

    public IRValueFunctionHandle(String name, List<IRDefValue> defs) {
        this.name = name;
        this.defs = List.copyOf(defs);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRValueFunctionHandle(" + name + ", " + defs.size() + ")";
    }
}
