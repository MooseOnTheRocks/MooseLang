package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.ASTExprTyped;

public class ASTStmtLet implements ASTStmt {
    private final ASTExprName name;
    private final ASTExprTyped<ASTExprName> typedName;
    public final ASTExpr body;

    public ASTStmtLet(ASTExprTyped<ASTExprName> typedName, ASTExpr body) {
        this.typedName = typedName;
        this.body = body;
        this.name = null;
    }

    public ASTStmtLet(ASTExprName name, ASTExpr body) {
        this.name = name;
        this.body = body;
        this.typedName = null;
    }

    public final ASTExpr getName() {
        return name == null ? typedName : name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
