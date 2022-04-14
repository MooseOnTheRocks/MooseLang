package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.List;

public class ASTExprFuncDef extends ASTExpr {
    public final ASTExprName name;
    public final List<ASTExprName> paramNames;
    public final ASTExpr body;

    public ASTExprFuncDef(ASTExprName name, List<ASTExprName> params, ASTExpr body) {
        this.name = name;
        this.paramNames = params;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprFuncDef{" +
                "name=" + name +
                ", params=" + paramNames +
                ", body=" + body +
                '}';
    }
}
