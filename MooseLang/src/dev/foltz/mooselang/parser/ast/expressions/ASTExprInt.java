package dev.foltz.mooselang.parser.ast.expressions;

public class ASTExprInt extends ASTExpr {
    public final int value;

    public ASTExprInt(int value) {
        this.value = value;
    }

    @Override
    public ASTExpr evalExpr() {
        return this;
    }

    @Override
    public String toString() {
        return "ASTExprNumber{" +
                "value=" + value +
                '}';
    }
}
