package dev.foltz.mooselang.parser.ast.statements;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;

public class ASTStmtForInDo extends ASTStmt {
    public ASTDeconstructor variableDecon;
    public ASTExpr listExpr;
    public ASTExpr body;

    public ASTStmtForInDo(ASTDeconstructor variableDecon, ASTExpr listExpr, ASTExpr body) {
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
        return "ASTStmtForInDo{" +
                "variableDecon=" + variableDecon +
                ", listExpr=" + listExpr +
                ", body=" + body +
                '}';
    }
}
