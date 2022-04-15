package dev.foltz.mooselang.parser.ast.expressions.literals;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTExprNone extends ASTExpr {
    public ASTExprNone() {
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
