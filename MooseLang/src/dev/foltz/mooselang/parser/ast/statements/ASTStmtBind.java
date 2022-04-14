package dev.foltz.mooselang.parser.ast.statements;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;

public class ASTStmtBind extends ASTStmt {
    public final ASTExprName name;
    public final ASTExpr expr;

    public ASTStmtBind(ASTExprName name, ASTExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtBind{" +
                "name=" + name +
                ", expr=" + expr +
                '}';
    }
}
