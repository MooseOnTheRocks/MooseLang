package dev.foltz.mooselang.parser.ast;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.*;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.statements.*;

public interface ASTVisitor<T> {
    T visit(ASTExprNone node);
    T visit(ASTExprBool node);
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    T visit(ASTExprList node);
    T visit(ASTExprName node);
    T visit(ASTExprBlock node);
    T visit(ASTExprAssign node);
    T visit(ASTExprCall node);
    T visit(ASTExprNegate node);
    T visit(ASTExprLetIn node);
    T visit(ASTExprIfThenElse node);
    T visit(ASTExprLambda node);
    T visit(ASTExprForInThenElse node);

    T visit(ASTStmtExpr node);
    T visit(ASTStmtLet node);
    T visit(ASTStmtFuncDef node);
    T visit(ASTStmtIfDo node);
    T visit(ASTStmtForInDo node);

    T visit(ASTDeconInt node);
    T visit(ASTDeconName node);
    T visit(ASTDeconString node);
    T visit(ASTDeconChar node);
    T visit(ASTDeconList node);
}
