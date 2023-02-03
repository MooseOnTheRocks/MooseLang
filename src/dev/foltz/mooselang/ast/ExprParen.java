package dev.foltz.mooselang.ast;

public class ExprParen extends ASTExpr {
    public final ASTExpr expr;

    public ExprParen(ASTExpr expr) {
        this.expr = expr;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Paren(" + expr + ")";
    }
}
