package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;

import java.util.List;

public record ASTStmtFuncDef(ASTExprName name, List<ASTExprName> params, ASTStmt body) implements ASTExpr {

    public ASTStmtFuncDef(ASTExprName name, List<ASTExprName> params, ASTStmt body) {
        this.name = name;
        this.params = List.copyOf(params);
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
