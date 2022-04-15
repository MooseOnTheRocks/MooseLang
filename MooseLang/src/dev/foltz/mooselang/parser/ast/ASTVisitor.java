package dev.foltz.mooselang.parser.ast;

import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconInt;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconList;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconName;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconString;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprList;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprNone;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtAssign;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

public interface ASTVisitor<T> {
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    T visit(ASTExprList node);
    T visit(ASTExprName node);
    T visit(ASTExprCall node);
    T visit(ASTExprBlock node);
    T visit(ASTStmtAssign node);
    T visit(ASTStmtExpr node);
    T visit(ASTExprNone node);
    T visit(ASTExprFuncDef node);
    T visit(ASTDeconInt node);
    T visit(ASTDeconName node);
    T visit(ASTDeconString node);
    T visit(ASTDeconList node);
}