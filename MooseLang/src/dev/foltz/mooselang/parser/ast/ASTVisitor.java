package dev.foltz.mooselang.parser.ast;

import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtBind;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

public interface ASTVisitor<T> {
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    T visit(ASTExprList node);
    T visit(ASTExprName node);
    T visit(ASTExprCall node);
    T visit(ASTExprBlock node);
    T visit(ASTStmtBind node);
    T visit(ASTStmtExpr node);
    T visit(ASTExprFuncDef node);
}
