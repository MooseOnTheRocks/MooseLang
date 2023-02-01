package dev.foltz.mooselang.ast;

public class ExprApply extends ASTExpr {
    public final ASTExpr lhs;
    public final ASTExpr rhs;

    public ExprApply(ASTExpr lhs, ASTExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "Apply(" + lhs + ", " + rhs + ")";
    }
}
