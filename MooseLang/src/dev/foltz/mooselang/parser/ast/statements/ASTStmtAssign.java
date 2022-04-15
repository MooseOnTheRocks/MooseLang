package dev.foltz.mooselang.parser.ast.statements;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;

public class ASTStmtAssign extends ASTStmt {
    public final ASTExprName name;
    public final ASTExpr expr;

    public ASTStmtAssign(ASTExprName name, ASTExpr body) {
        this.name = name;
        this.expr = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtAssign{" +
                "name=" + name +
                ", body=" + expr +
                '}';
    }
}
