package dev.foltz.mooselang.parser.ast.destructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;

public class ASTDestName extends ASTDestructor {
    public final ASTExprName name;

    public ASTDestName(ASTExprName name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDestName{" +
                "name=" + name +
                '}';
    }
}
