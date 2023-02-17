package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

import java.util.List;

public class ASTExprTuple extends ASTExpr {
    public final List<ASTExpr> values;

    public ASTExprTuple(List<ASTExpr> exprs) {
        this.values = exprs;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Tuple(" + values + ")";
    }
}
