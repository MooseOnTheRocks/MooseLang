package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ASTExprCall extends ASTExpr {
    public final ASTExprName name;
    public final List<ASTExpr> params;

    public ASTExprCall(ASTExprName name, List<ASTExpr> params) {
        this.name = name;
        this.params = List.copyOf(params);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprCall{" +
                "name='" + name + '\'' +
                ", params=" + params +
                '}';
    }
}
