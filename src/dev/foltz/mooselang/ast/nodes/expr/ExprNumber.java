package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprNumber extends ASTExpr {
    public final double value;

    public ExprNumber(double value) {
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Number(" + value + ")";
    }
}
