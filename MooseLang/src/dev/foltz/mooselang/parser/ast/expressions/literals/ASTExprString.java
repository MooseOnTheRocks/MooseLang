package dev.foltz.mooselang.parser.ast.expressions.literals;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTExprString extends ASTExpr {
    public final String value;

    public ASTExprString(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprString{" +
                "value='" + value + '\'' +
                '}';
    }
}
