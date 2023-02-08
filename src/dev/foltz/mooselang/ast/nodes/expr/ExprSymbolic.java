package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprSymbolic extends ASTExpr {
    public final String symbol;

    public ExprSymbolic(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Symbolic(" + symbol + ")";
    }
}
