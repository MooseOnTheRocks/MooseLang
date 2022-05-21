package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.ASTExprBool;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprNone;
import dev.foltz.mooselang.ast.expression.literals.ASTExprString;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.typing.ASTTypeLiteral;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;

import java.util.Arrays;
import java.util.List;

public class ASTPrinter extends ASTDefaultVisitor<StringBuilder> {
    private final StringBuilder sb;

    public ASTPrinter() {
        super((astNode) -> { throw new UnsupportedOperationException("Printer cannot visit " + astNode); });
        sb = new StringBuilder();
    }

    protected void emit() {
        sb.append("\n");
    }

    protected void emit(Object ...objs) {
        Arrays.stream(objs).forEach(obj -> {
            if (obj instanceof ASTNode node) {
                node.accept(this);
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

    public static String print(ASTNode node) {
        ASTPrinter printer = new ASTPrinter();
        return node.accept(printer).toString();
    }

    @Override
    public StringBuilder visit(ASTStmtLet node) {
        emit("StmtLet(", node.name, ", ", node.body, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprNone node) {
        emit("ExprNone()");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprBool node) {
        emit("ExprBool(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprInt node) {
        emit("ExprInt(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprName node) {
        if (node.typeHint().isPresent()) {
            emit("ExprName(", node.name, ": ", node.typeHint().get(), ")");
        }
        else {
            emit("ExprName(", node.name, ")");
        }
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprString node) {
        emit("ExprString(\"", node.value(), "\")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeName node) {
        emit("TypeName(", node.name, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeUnion node) {
        emit("TypeUnion(");
        emitJoin(", ", node.types);
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeLiteral node) {
        emit("TypeLiteral(", node.literal, ")");
        return sb;
    }
}
