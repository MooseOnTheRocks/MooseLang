package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.interpreter.runtime.RTString;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;

public class ASTDeconString extends ASTDeconstructor {
    public final ASTExprString literal;

    public ASTDeconString(ASTExprString literal) {
        this.literal = literal;
    }

    @Override
    public RTObject deconstruct(RTObject rtObj, Scope scope) {
        if (!matches(rtObj)) {
            throw new IllegalStateException("String deconstructor cannot accept: " + rtObj);
        }

        return rtObj;
    }

    @Override
    public boolean matches(RTObject rtObj) {
        if (rtObj instanceof RTString rtString) {
            return rtString.value.equals(literal.value);
        }

        return false;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDeconString{" +
                "literal=" + literal +
                '}';
    }
}
