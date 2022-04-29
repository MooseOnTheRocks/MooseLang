package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;

public record ASTStmtForInDo(ASTExprName name, ASTExpr listExpr, ASTExpr body) implements ASTStmt {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
