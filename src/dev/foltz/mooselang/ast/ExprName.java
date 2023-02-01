package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.parser.SourceDesc;

public class ExprName extends ASTExpr {
    public final String name;

    public ExprName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name(" + name + ")";
    }
}
