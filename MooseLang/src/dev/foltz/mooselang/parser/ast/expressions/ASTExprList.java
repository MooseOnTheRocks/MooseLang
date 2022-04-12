package dev.foltz.mooselang.parser.ast.expressions;

import java.util.ArrayList;
import java.util.List;

public class ASTExprList extends ASTExpr {
    public final List<ASTExpr> elements;

    public ASTExprList(List<ASTExpr> elements) {
        this.elements = List.copyOf(elements);
    }

    @Override
    public ASTExpr evalExpr() {
        List<ASTExpr> evaluated = new ArrayList<>();
        for (ASTExpr elem : elements) {
            evaluated.add(elem.evalExpr());
        }
        return new ASTExprList(evaluated);
    }

    @Override
    public String toString() {
        return "ASTExprList{" +
                "elements=" + elements +
                '}';
    }
}
