package dev.foltz.mooselang.ast;

public class ExprSymbolic extends ASTExpr {
    public final String symbol;

    public ExprSymbolic(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Symbolic(" + symbol + ")";
    }
}
