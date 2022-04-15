package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;

public class ASTDeconInt extends ASTDeconstructor {
    public final ASTExprInt literal;

    public ASTDeconInt(ASTExprInt literal) {
        this.literal = literal;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDestInt{" +
                "literal=" + literal +
                '}';
    }
}
