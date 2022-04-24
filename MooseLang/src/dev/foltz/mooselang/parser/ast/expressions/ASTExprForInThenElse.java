package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;

public class ASTExprForInThenElse extends ASTExpr {
    public ASTDeconstructor variableDecon;
    public ASTExpr listExpr;
    public ASTExpr bodyLoop, bodyElse;

    public ASTExprForInThenElse(ASTDeconstructor variableDecon, ASTExpr listExpr, ASTExpr bodyLoop, ASTExpr bodyElse) {
        this.variableDecon = variableDecon;
        this.listExpr = listExpr;
        this.bodyLoop = bodyLoop;
        this.bodyElse = bodyElse;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprForInThenElse{" +
                "variableDecon=" + variableDecon +
                ", listExpr=" + listExpr +
                ", bodyLoop=" + bodyLoop +
                ", bodyElse=" + bodyElse +
                '}';
    }
}
