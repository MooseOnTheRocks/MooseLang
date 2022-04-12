package dev.foltz.mooselang.parse.expressions;

import java.util.ArrayList;
import java.util.List;

public class ASTList extends ASTExpr {
    public final List<ASTExpr> elements;

    public ASTList(List<ASTExpr> elements) {
        this.elements = List.copyOf(elements);
    }

    @Override
    public ASTExpr evalExpr() {
        List<ASTExpr> evaluated = new ArrayList<>();
        for (ASTExpr elem : elements) {
            evaluated.add(elem.evalExpr());
        }
        return new ASTList(evaluated);
    }

    @Override
    public String toString() {
        return "ASTList{" +
                "elements=" + elements +
                '}';
    }
}
