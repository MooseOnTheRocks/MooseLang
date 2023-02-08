package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprDirective extends ASTExpr {
    public final ExprName name;
    public final ASTExpr body;

    public ExprDirective(ExprName name, ASTExpr body) {
        this.name = name;
        this.body = body;
    }


    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Directive(" + name + ", " + body + ")";
    }
}
