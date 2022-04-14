package dev.foltz.mooselang.parser.ast.statements;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTStmtExpr extends ASTStmt {
    public final ASTExpr expr;

    public ASTStmtExpr(ASTExpr expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtExpr{" +
                "expr=" + expr +
                '}';
    }
}
