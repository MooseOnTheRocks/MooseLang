package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTExprName extends ASTExpr {
    public final String value;

    public ASTExprName(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprName{" +
                "value='" + value + '\'' +
                '}';
    }
}
