package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

public class ASTExprFieldAccess implements ASTExpr {
    public final ASTExpr lhs;
    public final ASTExprName fieldName;

    public ASTExprFieldAccess(ASTExpr lhs, ASTExprName fieldName) {
        this.lhs = lhs;
        this.fieldName = fieldName;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
