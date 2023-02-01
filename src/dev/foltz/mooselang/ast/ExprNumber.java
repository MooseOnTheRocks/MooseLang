package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.parser.SourceDesc;

public class ExprNumber extends ASTExpr {
    public final double value;

    public ExprNumber(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Number(" + value + ")";
    }
}
