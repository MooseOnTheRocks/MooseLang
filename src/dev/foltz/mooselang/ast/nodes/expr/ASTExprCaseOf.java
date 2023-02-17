package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

import java.util.List;

public class ASTExprCaseOf extends ASTExpr {
    public final ASTExpr value;
    public final List<ASTExprCaseOfBranch> cases;

    public ASTExprCaseOf(ASTExpr value, List<ASTExprCaseOfBranch> cases) {
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
