package dev.foltz.mooselang.ast;

public class ExprLetValueIn extends ASTExpr {
    public final ExprName name;
    public final ASTExpr expr;
    public final ASTExpr body;

    public ExprLetValueIn(ExprName name, ASTExpr expr, ASTExpr body) {
        this.name = name;
        this.expr = expr;
        this.body = body;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "LetValueIn(" + name + ", " + expr + ", " + body + ")";
    }
}
