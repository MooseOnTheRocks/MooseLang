package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.parser.SourceDesc;

public class ExprNumber extends ASTExpr {
    public final double value;

    public ExprNumber(double value) {
        this.value = value;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Number(" + value + ")";
    }
}
