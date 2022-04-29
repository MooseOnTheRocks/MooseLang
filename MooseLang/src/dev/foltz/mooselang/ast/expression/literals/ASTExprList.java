package dev.foltz.mooselang.ast.expression.literals;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;

import java.util.List;

public record ASTExprList(List<ASTExpr> elements) implements ASTExpr {

    public ASTExprList(List<ASTExpr> elements) {
        this.elements = List.copyOf(elements);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
