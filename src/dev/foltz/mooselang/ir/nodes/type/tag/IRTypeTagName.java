package dev.foltz.mooselang.ir.nodes.type.tag;

import dev.foltz.mooselang.ir.VisitorIR;

public class IRTypeTagName extends IRTypeTag {
    public final String name;

    public IRTypeTagName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IRTypeTagName name && this.name.equals(name.name);
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRTypeTagName(" + name + ")";
    }
}
