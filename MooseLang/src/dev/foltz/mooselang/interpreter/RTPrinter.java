package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.interpreter.rt.RTBuiltinFunc;
import dev.foltz.mooselang.interpreter.rt.RTFuncDef;
import dev.foltz.mooselang.interpreter.rt.RTInt;
import dev.foltz.mooselang.interpreter.rt.RTObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RTPrinter extends RTDefaultVisitor<StringBuilder> {
    private final StringBuilder sb;

    public RTPrinter() {
        super(rt -> { throw new UnsupportedOperationException("Printer cannot visit: " + rt); });
        sb = new StringBuilder();
    }

    public static String print(RTObject node) {
        if (node == null) return "nil";
        RTPrinter printer = new RTPrinter();
        return node.accept(printer).toString();
    }

    protected void emit() {
        sb.append("\n");
    }

    protected void emit(Object ...objs) {
        Arrays.stream(objs).forEach(obj -> {
            if (obj instanceof RTObject rt) {
                rt.accept(this);
            }
            else {
                sb.append(obj);
            }
        });
    }

    protected void emitJoin(String sep, List<?> objs) {
        for (int i = 0; i < objs.size(); i++) {
            Object obj = objs.get(i);
            emit(obj);
            if (i != objs.size() - 1) {
                emit(", ");
            }
        }
    }

    @Override
    public StringBuilder visit(RTInt rt) {
        emit(rt.value);
        return sb;
    }

    @Override
    public StringBuilder visit(RTFuncDef rt) {
        emit("FuncDef(", String.join(", ", rt.paramNames), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(RTBuiltinFunc rt) {
        emit("Builtin(", rt.name, ")");
        return sb;
    }
}
