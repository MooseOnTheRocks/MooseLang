package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

public class ASTExprIfThenElse extends ASTExpr {
    public final ASTExpr exprCond;
    public final ASTExpr exprTrue;
    public final ASTExpr exprFalse;

    public ASTExprIfThenElse(ASTExpr exprCond, ASTExpr exprTrue, ASTExpr exprFalse) {
        this.exprCond = exprCond;
        this.exprTrue = exprTrue;
        this.exprFalse = exprFalse;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprIfElseThen{" +
                "exprCond=" + exprCond +
                ", exprTrue=" + exprTrue +
                ", exprFalse=" + exprFalse +
                '}';
    }
}
