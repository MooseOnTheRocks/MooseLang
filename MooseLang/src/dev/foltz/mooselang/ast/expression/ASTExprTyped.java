package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.typing.ASTType;

public class ASTExprTyped<T extends ASTExpr> implements ASTExpr {
    public final T expr;
    public final ASTType type;

    public ASTExprTyped(T expr, ASTType type) {
        this.expr = expr;
        this.type = type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
