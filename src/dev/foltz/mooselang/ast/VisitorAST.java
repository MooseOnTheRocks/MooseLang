package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtLet;
import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeName;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeTuple;

public abstract class VisitorAST<T> {
    // AST Types
    public T visit(ASTTypeName name) { return undefined(name); }
    public T visit(ASTTypeTuple tuple) { return undefined(tuple); }

    // AST Expressions
    public T visit(ASTExprApply apply) { return undefined(apply); }
    public T visit(ASTExprCaseOf caseOf) { return undefined(caseOf); }
    public T visit(ASTExprCaseOfBranch ofBranch) { return undefined(ofBranch); }
    public T visit(ASTExprChain chain) { return undefined(chain); }
    public T visit(ASTExprLambda lambda) { return undefined(lambda); }
    public T visit(ASTExprLetIn letIn) { return undefined(letIn); }
    public T visit(ASTExprName name) { return undefined(name); }
    public T visit(ASTExprNumber number) { return undefined(number); }
    public T visit(ASTExprString string) { return undefined(string); }
    public T visit(ASTExprParen paren) { return undefined(paren); }
    public T visit(ASTExprSymbolic symbolic) { return undefined(symbolic); }
    public T visit(ASTExprTuple tuple) { return undefined(tuple); }

    // AST Statements
    public T visit(ASTStmtDef def) { return undefined(def); }
    public T visit(ASTStmtLet let) { return undefined(let); }

    public T undefined(ASTNode node) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot visit " + node);
    }
}
