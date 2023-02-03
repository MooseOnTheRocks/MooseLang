package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.parser.SourceDesc;

public class ExprName extends ASTExpr {
    public final String name;

    public ExprName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Name(" + name + ")";
    }
}
