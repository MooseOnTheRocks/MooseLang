package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTNode;

public interface ASTStmt extends ASTNode {
    @Override
    default ASTStmt copy() {
        return this;
    }
}
