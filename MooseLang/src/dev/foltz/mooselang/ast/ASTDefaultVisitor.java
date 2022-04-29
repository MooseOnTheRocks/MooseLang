package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.statement.*;

import java.util.function.Function;

public class ASTDefaultVisitor<T> implements ASTVisitor<T> {
    public final Function<ASTNode, T> defaultOp;

    public ASTDefaultVisitor(Function<ASTNode, T> defaultOp) {
        this.defaultOp = defaultOp;
    }

    @Override
    public T visit(ASTExprNone node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprBool node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprInt node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprString node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprList node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprName node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprCall node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprBlock node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprAssign node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprLetIn node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprIfThenElse node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprForInThenElse node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprLambda node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTExprNegate node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTStmtExpr node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTStmtFuncDef node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTStmtLet node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTStmtIfDo node) {
        return defaultOp.apply(node);
    }

    @Override
    public T visit(ASTStmtForInDo node) {
        return defaultOp.apply(node);
    }
}
