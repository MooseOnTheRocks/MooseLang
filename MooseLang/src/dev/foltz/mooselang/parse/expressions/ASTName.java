package dev.foltz.mooselang.parse.expressions;

public class ASTName extends ASTExpr {
    public final String value;

    public ASTName(String value) {
        this.value = value;
    }

    @Override
    public ASTExpr evalExpr() {
        return this;
    }

    @Override
    public String toString() {
        return "ASTName{" +
                "value='" + value + '\'' +
                '}';
    }
}
