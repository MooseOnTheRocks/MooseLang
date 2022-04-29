package dev.foltz.mooselang.ast;

public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}
