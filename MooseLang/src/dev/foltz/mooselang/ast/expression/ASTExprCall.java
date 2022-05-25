package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;

import java.util.List;

public class ASTExprCall implements ASTExpr {
    public final ASTExprName name;
    public final List<ASTExpr> params;

    public ASTExprCall(ASTExprName name, List<ASTExpr> params) {
        this.name = name;
        this.params = List.copyOf(params);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
