package dev.foltz.mooselang.parser.ast.expressions;

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
    public ASTExpr evalExpr() {
        List<ASTExpr> evaluated = new ArrayList<>();
        for (ASTExpr param : params) {
            evaluated.add(param.evalExpr());
        }
        return new ASTExprCall(name, evaluated);
    }

    @Override
    public String toString() {
        return "ASTExprCall{" +
                "name='" + name + '\'' +
                ", params=" + params +
                '}';
    }
}
