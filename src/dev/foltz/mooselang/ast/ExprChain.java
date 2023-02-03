package dev.foltz.mooselang.ast;

public class ExprChain extends ASTExpr {
    public final ASTExpr first;
    public final ASTExpr second;

    public ExprChain(ASTExpr first, ASTExpr second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ExprChain(" + first + ", " + second + ")";
    }
}
