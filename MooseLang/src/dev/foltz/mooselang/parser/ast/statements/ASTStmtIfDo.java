package dev.foltz.mooselang.parser.ast.statements;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTStmtIfDo extends ASTStmt {
    public ASTExpr exprCond;
    public ASTExpr exprTrue;

    public ASTStmtIfDo(ASTExpr exprCond, ASTExpr exprTrue) {
        this.exprCond = exprCond;
        this.exprTrue = exprTrue;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtIfDo{" +
                "exprCond=" + exprCond +
                ", exprTrue=" + exprTrue +
                '}';
    }
}
