package dev.foltz.mooselang.ast;

public class ExprDirective extends ASTExpr {
    public final ExprName name;
    public final ASTExpr body;

    public ExprDirective(ExprName name, ASTExpr body) {
        this.name = name;
        this.body = body;
    }


    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Directive(" + name + ", " + body + ")";
    }
}
