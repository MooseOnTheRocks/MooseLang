package dev.foltz.mooselang.ast.typing;

import dev.foltz.mooselang.ast.ASTVisitor;

public class ASTTypeName implements ASTType {
    public final String name;

    public ASTTypeName(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
