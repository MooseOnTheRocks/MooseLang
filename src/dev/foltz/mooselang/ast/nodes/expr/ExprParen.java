package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprParen extends ASTExpr {
    public final ASTExpr expr;

    public ExprParen(ASTExpr expr) {
        this.expr = expr;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Paren(" + expr + ")";
    }
}
