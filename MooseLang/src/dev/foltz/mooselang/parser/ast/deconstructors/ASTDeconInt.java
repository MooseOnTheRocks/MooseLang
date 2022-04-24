package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.interpreter.runtime.RTInt;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;

public class ASTDeconInt extends ASTDeconstructor {
    public final ASTExprInt literal;

    public ASTDeconInt(ASTExprInt literal) {
        this.literal = literal;
    }

    @Override
    public RTObject deconstruct(RTObject rtObj, Scope scope) {
        if (!matches(rtObj)) {
            throw new IllegalStateException("Int deconstructor cannot accept: " + rtObj);
        }

        return rtObj;
    }

    @Override
    public boolean matches(RTObject rtObj) {
        return rtObj instanceof RTInt rtInt && rtInt.value == literal.value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDeconInt{" +
                "literal=" + literal +
                '}';
    }
}
