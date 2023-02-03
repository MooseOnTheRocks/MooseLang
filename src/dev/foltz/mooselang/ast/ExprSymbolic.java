package dev.foltz.mooselang.ast;

public class ExprSymbolic extends ASTExpr {
    public final String symbol;

    public ExprSymbolic(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Symbolic(" + symbol + ")";
    }
}
