package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.interpreter.rt.RTBuiltinFunc;
import dev.foltz.mooselang.interpreter.rt.RTFuncDef;
import dev.foltz.mooselang.interpreter.rt.RTInt;
import dev.foltz.mooselang.interpreter.rt.RTObject;

import java.util.function.Function;

public class RTDefaultVisitor<T> implements RTVisitor<T> {
    public final Function<RTObject, T> defaultOp;

    public RTDefaultVisitor(Function<RTObject, T> defaultOp) {
        this.defaultOp = defaultOp;
    }


    @Override
    public T visit(RTInt rt) {
        return defaultOp.apply(rt);
    }

    @Override
    public T visit(RTFuncDef rt) {
        return defaultOp.apply(rt);
    }

    @Override
    public T visit(RTBuiltinFunc rt) {
        return defaultOp.apply(rt);
    }
}
