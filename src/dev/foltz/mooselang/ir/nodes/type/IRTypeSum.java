package dev.foltz.mooselang.ir.nodes.type;

import dev.foltz.mooselang.ir.VisitorIR;

import java.util.List;

public class IRTypeSum extends IRType {
    public final String typeName;
    public List<String> conNames;
    public List<List<IRType>> params;

    public IRTypeSum(String typeName, List<String> conNames, List<List<IRType>> params) {
        this.typeName = typeName;
        this.conNames = List.copyOf(conNames);
        this.params = List.copyOf(params);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRTypeSum sum && sum.typeName.equals(typeName);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeSum(" + typeName + ", " + conNames + ", " + params + ")";
    }
}
