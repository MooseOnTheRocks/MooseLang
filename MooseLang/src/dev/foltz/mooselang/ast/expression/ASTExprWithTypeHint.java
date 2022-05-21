package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.typing.ASTType;

import java.util.Optional;

public abstract class ASTExprWithTypeHint implements ASTExpr {
    private ASTType typeHint;

    public ASTExprWithTypeHint() {
        this.typeHint = null;
    }

    public ASTExprWithTypeHint(ASTType typeHint) {
        this.typeHint = typeHint;
    }

    @Override
    public ASTExprWithTypeHint withTypeHint(ASTType hint) {
        if (this.typeHint().isPresent()) {
            throw new IllegalArgumentException("Cannot add type hint to already typed expression: " + this + ", " + hint);
        }

        var clone = copy();
        clone.typeHint = hint;
        return clone;
    }

    @Override
    public Optional<ASTType> typeHint() {
        return Optional.ofNullable(typeHint);
    }

    @Override
    public ASTExprWithTypeHint copy() {
        return this;
    }
}
