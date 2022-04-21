package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;

public class ASTExprForInLoop extends ASTExpr {
    public ASTDeconstructor variableDecon;
    public ASTExpr listExpr;
    public ASTExpr body;

    public ASTExprForInLoop(ASTDeconstructor variableDecon, ASTExpr listExpr, ASTExpr body) {
        this.variableDecon = variableDecon;
        this.listExpr = listExpr;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtForInLoop{" +
                "variableDecon=" + variableDecon +
                ", listExpr=" + listExpr +
                ", body=" + body +
                '}';
    }
}
