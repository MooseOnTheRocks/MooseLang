package dev.foltz.mooselang.ast.typing;

import dev.foltz.mooselang.ast.ASTVisitor;

import java.util.List;

public class ASTTypeUnion implements ASTType {
    public final List<ASTType> types;

    public ASTTypeUnion(List<ASTType> types) {
        this.types = List.copyOf(types);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
