package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprApply extends ASTExpr {
    public final ASTExpr lhs;
    public final ASTExpr rhs;

    public ExprApply(ASTExpr lhs, ASTExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Apply(" + lhs + ", " + rhs + ")";
    }
}
