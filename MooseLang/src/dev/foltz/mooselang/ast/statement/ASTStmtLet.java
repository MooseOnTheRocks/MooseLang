package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;

public class ASTStmtLet implements ASTStmt {
    public final ASTExprName name;
    public final ASTExpr body;

    public ASTStmtLet(ASTExprName name, ASTExpr body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
