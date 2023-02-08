package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprLetCompIn extends ASTExpr {
    public final ASTExpr expr;
    public final ExprName name;
    public final ASTExpr body;

    public ExprLetCompIn(ASTExpr expr, ExprName name, ASTExpr body) {
        this.expr = expr;
        this.name = name;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LetCompIn(" + expr + ", " + name + ", " + body + ")";
    }
}
