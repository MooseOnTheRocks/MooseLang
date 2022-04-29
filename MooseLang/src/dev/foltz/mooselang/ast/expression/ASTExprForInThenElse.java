package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

public record ASTExprForInThenElse(
        ASTExprName name,
        ASTExpr listExpr,
        ASTExpr bodyLoop,
        ASTExpr bodyElse
) implements ASTExpr {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
