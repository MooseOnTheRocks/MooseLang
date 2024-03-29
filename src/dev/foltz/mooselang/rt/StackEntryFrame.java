package dev.foltz.mooselang.rt;

import dev.foltz.mooselang.ir.nodes.comp.IRComp;

public class StackEntryFrame extends StackEntry {
    public final String name;
    public final IRComp body;

    public StackEntryFrame(String name, IRComp body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public String toString() {
        return "StackFrame(" + name + ", " + body + ")";
    }
}
