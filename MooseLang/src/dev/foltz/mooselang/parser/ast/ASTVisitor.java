package dev.foltz.mooselang.parser.ast;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprList;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprNone;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;
import dev.foltz.mooselang.parser.ast.statements.*;

public interface ASTVisitor<T> {
    T visit(ASTExprNone node);
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    T visit(ASTExprList node);
    T visit(ASTExprName node);
    T visit(ASTExprBlock node);
    T visit(ASTExprAssign node);
    T visit(ASTExprCall node);
    T visit(ASTExprNegate node);
    T visit(ASTExprLetIn node);
    T visit(ASTExprForInLoop node);

    T visit(ASTExprLambda node);
    T visit(ASTStmtExpr node);
    T visit(ASTStmtLet node);
    T visit(ASTStmtFuncDef node);

    T visit(ASTDeconInt node);
    T visit(ASTDeconName node);
    T visit(ASTDeconString node);
    T visit(ASTDeconList node);
}
