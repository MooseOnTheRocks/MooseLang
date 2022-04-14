package dev.foltz.mooselang.parser.ast.destructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprString;

public class ASTDestString extends ASTDestructor {
    public final ASTExprString value;

    public ASTDestString(ASTExprString value) {
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
