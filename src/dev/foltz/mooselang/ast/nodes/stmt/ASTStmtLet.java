package dev.foltz.mooselang.ast.nodes.stmt;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ASTExprName;

public class ASTStmtLet extends ASTStmt {
    public final ASTExprName name;
    public final ASTExpr expr;

    public ASTStmtLet(ASTExprName name, ASTExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Let(" + name + ", " + expr + ")";
    }
}
