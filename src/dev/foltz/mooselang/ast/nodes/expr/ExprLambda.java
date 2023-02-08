package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprLambda extends ASTExpr {
    public final String param;
    public final String paramType;
    public final ASTExpr body;

    public ExprLambda(String param, String paramType, ASTExpr body) {
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
