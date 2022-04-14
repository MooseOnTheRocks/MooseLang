package dev.foltz.mooselang.parser.ast.destructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprInt;

public class ASTDestInt extends ASTDestructor {
    public final ASTExprInt literal;

    public ASTDestInt(ASTExprInt literal) {
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
