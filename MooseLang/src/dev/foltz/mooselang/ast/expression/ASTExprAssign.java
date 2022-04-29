package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

public record ASTExprAssign(ASTExprName name, ASTExpr expr) implements ASTExpr {

    public ASTExprAssign(ASTExprName name, ASTExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
