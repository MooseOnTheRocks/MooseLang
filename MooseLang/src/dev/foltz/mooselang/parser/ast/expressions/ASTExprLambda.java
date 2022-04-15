package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

import java.util.List;

public class ASTExprLambda extends ASTExpr {
    public final List<ASTDeconstructor> paramDtors;
    public final ASTStmt body;

    public ASTExprLambda(List<ASTDeconstructor> paramDtors, ASTStmt body) {
        this.paramDtors = paramDtors;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
