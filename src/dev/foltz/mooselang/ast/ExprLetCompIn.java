package dev.foltz.mooselang.ast;

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
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LetCompIn(" + expr + ", " + name + ", " + body + ")";
    }
}
