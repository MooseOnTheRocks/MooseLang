package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprChain extends ASTExpr {
    public final ASTExpr first;
    public final ASTExpr second;

    public ExprChain(ASTExpr first, ASTExpr second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ExprChain(" + first + ", " + second + ")";
    }
}
