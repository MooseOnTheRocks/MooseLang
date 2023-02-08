package dev.foltz.mooselang.ast.nodes;

import dev.foltz.mooselang.ast.VisitorAST;

public abstract class ASTNode {
    public abstract <T> T apply(VisitorAST<T> visitor);
}
