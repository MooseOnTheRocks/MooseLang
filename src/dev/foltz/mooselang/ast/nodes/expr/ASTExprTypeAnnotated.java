package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeName;

public class ASTExprTypeAnnotated extends ASTExpr {
    public final ASTExpr expr;
    public final ASTType type;

    public ASTExprTypeAnnotated(ASTExpr expr, ASTType type) {
        this.expr = expr;
        this.type = type;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprTypeAnnotated(" + expr + ", " + type + ")";
    }
}
