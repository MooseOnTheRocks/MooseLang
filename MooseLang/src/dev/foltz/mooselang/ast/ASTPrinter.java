package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.statement.ASTStmtExpr;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;

import java.util.Arrays;

public class ASTPrinter extends ASTDefaultVisitor<String> {
    private final StringBuilder sb;

    public ASTPrinter() {
        super((astNode) -> { throw new UnsupportedOperationException("Printer cannot visit " + astNode); });
        sb = new StringBuilder();
    }

    public static String asString(ASTNode node) {
        ASTPrinter printer = new ASTPrinter();
        return node.accept(printer);
    }

    protected void emit() {
        sb.append("\n");
    }

    protected void emit(Object ...objs) {
        Arrays.stream(objs).forEach(obj -> {
            if (obj instanceof ASTNode node) {
                System.out.print(node.accept(this));
            }
            else {
                System.out.print(obj);
            }
        });
    }

    @Override
    public String visit(ASTExprInt node) {
        emit("ExprInt(");
        emit(node.value());
        emit(")");
        return sb.toString();
    }

    @Override
    public String visit(ASTExprName node) {
        emit("ExprName(", node.value(), ")");
        return sb.toString();
    }

    @Override
    public String visit(ASTStmtLet node) {
        emit("StmtLet(", node.name(), ", ", node.expr(), ")");
        return sb.toString();
    }
}
