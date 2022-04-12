package dev.foltz.mooselang.parse.expressions;

import java.util.ArrayList;
import java.util.List;

public class ASTCall extends ASTExpr {
    public final ASTName name;
    public final List<ASTExpr> params;

    public ASTCall(ASTName name, List<ASTExpr> params) {
        this.name = name;
        this.params = List.copyOf(params);
    }

    @Override
    public ASTExpr evalExpr() {
        List<ASTExpr> evaluated = new ArrayList<>();
        for (ASTExpr param : params) {
            evaluated.add(param.evalExpr());
        }
        return new ASTCall(name, evaluated);
    }

    @Override
    public String toString() {
        return "ASTCall{" +
                "name='" + name + '\'' +
                ", params=" + params +
                '}';
    }
}
