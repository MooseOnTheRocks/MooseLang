package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

public class ASTExprLetIn extends ASTExpr {
    public final ASTExprName name;
    public final ASTExpr expr;
    public final ASTExpr body;

    public ASTExprLetIn(ASTExprName name, ASTExpr expr, ASTExpr body) {
        this.name = name;
        this.expr = expr;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtLetIn{" +
                "name=" + name +
                ", expr=" + expr +
                ", body=" + body +
                '}';
    }
}
