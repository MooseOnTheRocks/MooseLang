package dev.foltz.mooselang.parse.expressions;

public class ASTInt extends ASTExpr {
    public final int value;

    public ASTInt(int value) {
        this.value = value;
    }

    @Override
    public ASTExpr evalExpr() {
        return this;
    }

    @Override
    public String toString() {
        return "ASTNumber{" +
                "value=" + value +
                '}';
    }
}
