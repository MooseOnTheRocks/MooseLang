package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ASTExprCaseOfBranch extends ASTExpr {
    public final ASTExpr pattern;
    public final ASTExpr body;

    public ASTExprCaseOfBranch(ASTExpr pattern, ASTExpr body) {
        this.pattern = pattern;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ExprCaseOfBranch(" + pattern + ", " + body + ")";
    }
}
