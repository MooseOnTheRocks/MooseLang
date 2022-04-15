package dev.foltz.mooselang.parser.ast.expressions.literals;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTExprInt extends ASTExpr {
    public final int value;

    public ASTExprInt(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprNumber{" +
                "value=" + value +
                '}';
    }
}
