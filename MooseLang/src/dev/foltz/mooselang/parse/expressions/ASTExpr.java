package dev.foltz.mooselang.parse.expressions;

import dev.foltz.mooselang.parse.AST;

public abstract class ASTExpr extends AST {
    public abstract ASTExpr evalExpr();
}
