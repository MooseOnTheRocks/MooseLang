package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprLetIn extends ASTExpr {
    public final ExprName name;
    public final ASTExpr expr;
    public final ASTExpr body;

    public ExprLetIn(ExprName name, ASTExpr expr, ASTExpr body) {
        this.name = name;
        this.expr = expr;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LetIn(" + name + ", " + expr + ", " + body + ")";
    }
}
