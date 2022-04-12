package dev.foltz.mooselang.parse.statements;

import dev.foltz.mooselang.parse.expressions.ASTExpr;
import dev.foltz.mooselang.parse.expressions.ASTName;

public class ASTBind extends ASTStmt {
    public final ASTName name;
    public final ASTExpr expr;

    public ASTBind(ASTName name, ASTExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public ASTStmt evalStmt() {
        return new ASTBind(name, expr.evalExpr());
    }

    @Override
    public String toString() {
        return "ASTBind{" +
                "name=" + name +
                ", expr=" + expr +
                '}';
    }
}
