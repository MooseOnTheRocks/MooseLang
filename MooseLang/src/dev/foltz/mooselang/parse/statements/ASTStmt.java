package dev.foltz.mooselang.parse.statements;

import dev.foltz.mooselang.parse.AST;

public abstract class ASTStmt extends AST {
    public abstract ASTStmt evalStmt();
}
