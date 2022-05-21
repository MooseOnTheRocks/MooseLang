package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.ASTVisitor;

public class ASTExprName extends ASTExprWithTypeHint {
    public final String name;

    public ASTExprName(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
