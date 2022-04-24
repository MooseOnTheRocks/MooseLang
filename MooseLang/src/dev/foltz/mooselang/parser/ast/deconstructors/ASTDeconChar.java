package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.interpreter.runtime.RTString;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;

import java.util.Objects;

public class ASTDeconChar extends ASTDeconstructor {
    public final ASTExprString literal;

    public ASTDeconChar(ASTExprString literal) {
        this.literal = literal;
    }

    @Override
    public RTObject deconstruct(RTObject rtObj, Scope scope) {
        if (!matches(rtObj)) {
            throw new IllegalStateException("Char deconstructor cannot accept: " + rtObj);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof ASTDeconChar deconChar) {
            return Objects.equals(literal.value, deconChar.literal.value);
        }
        else if (o instanceof ASTDeconString deconString) {
            return Objects.equals(literal.value, deconString.literal.value);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal);
    }

    @Override
    public String toString() {
        return "ASTDeconDecon{" +
                "literal=" + literal +
                '}';
    }
}
