package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

public record ASTExprName(String value) implements ASTExpr {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
