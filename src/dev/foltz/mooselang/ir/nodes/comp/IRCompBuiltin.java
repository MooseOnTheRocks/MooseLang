package dev.foltz.mooselang.ir.nodes.comp;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.comp.TypeComp;

import java.util.function.Function;

public class IRCompBuiltin extends IRComp {
    public final String name;
    public TypeComp innerType;
    public final Function<Interpreter, Interpreter> internal;

    public IRCompBuiltin(String name, TypeComp innerType, Function<Interpreter, Interpreter> internal) {
        this.name = name;
        this.innerType = innerType;
        this.internal = internal;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRBuiltin(" + name + ", " + innerType + ")";
    }
}
