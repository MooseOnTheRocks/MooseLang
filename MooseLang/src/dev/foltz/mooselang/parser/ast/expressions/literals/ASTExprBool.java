package dev.foltz.mooselang.parser.ast.expressions.literals;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTExprBool extends ASTExpr {
    public final boolean value;

    public ASTExprBool(boolean value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprBool{" +
                "value=" + value +
                '}';
    }
}
