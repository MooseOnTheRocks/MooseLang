package dev.foltz.mooselang.ir.nodes.builtin;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.comp.IRLambda;
import dev.foltz.mooselang.ir.nodes.comp.IRProduce;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.comp.CompLambda;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.comp.TypeComp;
import dev.foltz.mooselang.typing.value.*;

import java.util.function.Function;

public class IRBuiltin extends IRComp {
    public final String name;
    public TypeComp innerType;
    public final Function<Interpreter, Interpreter> internal;

    public IRBuiltin(String name, TypeComp innerType, Function<Interpreter, Interpreter> internal) {
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
