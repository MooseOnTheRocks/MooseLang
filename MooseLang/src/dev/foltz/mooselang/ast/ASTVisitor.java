package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.statement.*;

public interface ASTVisitor<T> {
    T visit(ASTExprNone node);
    T visit(ASTExprBool node);
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    T visit(ASTExprList node);

    T visit(ASTExprName node);
    T visit(ASTExprCall node);
    T visit(ASTExprBlock node);
    T visit(ASTExprAssign node);
    T visit(ASTExprLetIn node);
    T visit(ASTExprIfThenElse node);
    T visit(ASTExprForInThenElse node);
    T visit(ASTExprLambda node);
    T visit(ASTExprNegate node);

    T visit(ASTStmtExpr node);
    T visit(ASTStmtFuncDef node);
    T visit(ASTStmtLet node);
    T visit(ASTStmtIfDo node);
    T visit(ASTStmtForInDo node);
}
