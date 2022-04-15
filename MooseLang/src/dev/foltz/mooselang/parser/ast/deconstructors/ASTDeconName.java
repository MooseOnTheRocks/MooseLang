package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;

public class ASTDeconName extends ASTDeconstructor {
    public final ASTExprName name;

    public ASTDeconName(ASTExprName name) {
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
