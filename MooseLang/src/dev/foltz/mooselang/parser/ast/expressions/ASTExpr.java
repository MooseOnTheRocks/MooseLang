package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTNode;

public abstract class ASTExpr extends ASTNode {
    public abstract ASTExpr evalExpr();
}
