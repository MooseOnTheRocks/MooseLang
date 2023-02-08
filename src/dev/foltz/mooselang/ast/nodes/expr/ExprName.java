package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprName extends ASTExpr {
    public final String name;

    public ExprName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Name(" + name + ")";
    }
}
