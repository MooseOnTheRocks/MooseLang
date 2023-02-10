package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

import java.util.List;

public class ExprCaseOf extends ASTExpr {
    public final ASTExpr value;
    public final List<ExprCaseOfBranch> cases;

    public ExprCaseOf(ASTExpr value, List<ExprCaseOfBranch> cases) {
        this.value = value;
        this.cases = List.copyOf(cases);
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ExprCaseOf(" + value + ", " + cases + ")";
    }
}
