package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.statement.ASTStmt;

import java.util.List;

public record ASTExprLambda(
        List<ASTExprName> paramNames,
        ASTStmt body
) implements ASTExpr {

    public ASTExprLambda(List<ASTExprName> paramNames, ASTStmt body) {
        this.paramNames = List.copyOf(paramNames);
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
