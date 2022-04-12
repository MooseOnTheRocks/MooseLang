package dev.foltz.mooselang.parser.ast.expressions;

public class ASTExprName extends ASTExpr {
    public final String value;

    public ASTExprName(String value) {
        this.value = value;
    }

    @Override
    public ASTExpr evalExpr() {
        return this;
    }

    @Override
    public String toString() {
        return "ASTExprName{" +
                "value='" + value + '\'' +
                '}';
    }
}
