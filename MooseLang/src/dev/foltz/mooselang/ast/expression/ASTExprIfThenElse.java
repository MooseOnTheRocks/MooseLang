package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

public record ASTExprIfThenElse(
        ASTExpr exprCond,
        ASTExpr exprTrue,
        ASTExpr exprFalse
) implements ASTExpr {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
