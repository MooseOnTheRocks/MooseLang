package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.rt.RTBuiltinFunc;
import dev.foltz.mooselang.interpreter.rt.RTFuncDef;
import dev.foltz.mooselang.interpreter.rt.RTInt;

public interface RTVisitor<T> {
    T visit(RTInt rt);
    T visit(RTFuncDef rt);
    T visit(RTBuiltinFunc rt);
}
