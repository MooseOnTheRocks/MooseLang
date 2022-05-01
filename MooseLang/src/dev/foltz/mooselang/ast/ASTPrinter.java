package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.ASTExprBlock;
import dev.foltz.mooselang.ast.expression.ASTExprCall;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.statement.ASTStmtExpr;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ASTPrinter extends ASTDefaultVisitor<StringBuilder> {
    private final StringBuilder sb;

    public ASTPrinter() {
        super((astNode) -> { throw new UnsupportedOperationException("Printer cannot visit " + astNode); });
        sb = new StringBuilder();
    }

    public static String print(ASTNode node) {
        ASTPrinter printer = new ASTPrinter();
        return node.accept(printer).toString();
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

    @Override
    public StringBuilder visit(ASTExprInt node) {
        emit("ExprInt(");
        emit(node.value());
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprName node) {
        emit("ExprName(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtLet node) {
        emit("StmtLet(", node.name(), ", ", node.expr(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtExpr node) {
        emit("StmtExpr(", node.expr(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprBlock node) {
        emit("ExprBlock(");
        emitJoin(", ", node.stmts());
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtFuncDef node) {
        emit("StmtFuncDef(", node.name(), ", (");
        emitJoin(", ", node.params());
        emit("), ", node.body(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprCall node) {
        emit("ExprCall(", node.name(), ", (");
        emitJoin(", ", node.params());
        emit("))");
        return sb;
    }
}
