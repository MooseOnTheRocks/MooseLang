package dev.foltz.mooselang.parse.statements;

import dev.foltz.mooselang.parse.expressions.ASTExpr;

public class ASTStmtExpr extends ASTStmt {
    public final ASTExpr expr;

    public ASTStmtExpr(ASTExpr expr) {
        this.expr = expr;
    }

    @Override
    public ASTStmt evalStmt() {
        return new ASTStmtExpr(expr.evalExpr());
    }

    @Override
    public String toString() {
        return "ASTStmtExpr{" +
                "expr=" + expr +
                '}';
    }
}
