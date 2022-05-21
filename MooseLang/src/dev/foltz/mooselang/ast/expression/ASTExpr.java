package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.typing.ASTType;

import java.util.Optional;

public interface ASTExpr extends ASTNode {
    Optional<ASTType> typeHint();

    default ASTExprWithTypeHint withTypeHint(ASTType hint) {
        throw new UnsupportedOperationException("withTypeHint not supported.");
    }

    @Override
    default ASTExpr copy() {
        return this;
    }
}
