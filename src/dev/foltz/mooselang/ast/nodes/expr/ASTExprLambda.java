package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.type.ASTType;

public class ASTExprLambda extends ASTExpr {
    public final String param;
    public final ASTType paramType;
    public final ASTExpr body;

    public ASTExprLambda(String param, ASTType paramType, ASTExpr body) {
        this.param = param;
        this.paramType = paramType;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Lambda(" + param + ", " + paramType + ", " + body + ")";
    }
}
