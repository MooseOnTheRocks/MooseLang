package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

public class ASTExprNegate extends ASTExpr {
    public final ASTExpr expr;

    public ASTExprNegate(ASTExpr expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprNegate{" +
                "expr=" + expr +
                '}';
    }
}
