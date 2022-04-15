package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;

public class ASTDeconString extends ASTDeconstructor {
    public final ASTExprString value;

    public ASTDeconString(ASTExprString value) {
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDestString{" +
                "value=" + value +
                '}';
    }
}
