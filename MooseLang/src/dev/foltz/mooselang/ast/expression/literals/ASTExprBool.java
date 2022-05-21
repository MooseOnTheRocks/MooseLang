package dev.foltz.mooselang.ast.expression.literals;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.ast.typing.ASTTypeName;

import java.util.Optional;

public record ASTExprBool(boolean value) implements ASTExpr {
    public static final ASTType AST_TYPE_BOOL = new ASTTypeName("Bool");

    @Override
    public Optional<ASTType> typeHint() {
        return Optional.of(AST_TYPE_BOOL);
    }

    @Override
    public ASTExprBool copy() {
        return this;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
