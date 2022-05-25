package dev.foltz.mooselang.ast.typing;

import dev.foltz.mooselang.ast.ASTVisitor;

public record ASTTypeName(String name) implements ASTType {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
