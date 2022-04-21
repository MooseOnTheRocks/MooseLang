package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

public class ASTExprAssign extends ASTExpr {
    public final ASTExprName name;
    public final ASTExpr expr;

    public ASTExprAssign(ASTExprName name, ASTExpr body) {
        this.name = name;
        this.expr = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprAssign{" +
                "name=" + name +
                ", body=" + expr +
                '}';
    }
}
