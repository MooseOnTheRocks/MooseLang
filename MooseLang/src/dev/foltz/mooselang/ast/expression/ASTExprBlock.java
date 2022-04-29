package dev.foltz.mooselang.ast.expression;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.statement.ASTStmt;

import java.util.List;

public record ASTExprBlock(List<ASTStmt> stmts) implements ASTExpr {

    public ASTExprBlock(List<ASTStmt> stmts) {
        this.stmts = List.copyOf(stmts);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
