package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.stmt.StmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.StmtLet;
import dev.foltz.mooselang.ast.nodes.expr.*;

public abstract class VisitorAST<T> {
    public T visit(ExprApply apply) { return undefined(apply); }
    public T visit(ExprChain chain) { return undefined(chain); }
    public T visit(ExprDirective directive) { return undefined(directive); }
    public T visit(ExprLambda lambda) { return undefined(lambda); }
    public T visit(ExprLetCompIn letIn) { return undefined(letIn); }
    public T visit(ExprLetValueIn letIn) { return undefined(letIn); }
    public T visit(ExprName name) { return undefined(name); }
    public T visit(ExprNumber number) { return undefined(number); }
    public T visit(ExprString string) { return undefined(string); }
    public T visit(ExprParen paren) { return undefined(paren); }
    public T visit(ExprSymbolic symbolic) { return undefined(symbolic); }
    
    public T visit(StmtDef def) { return undefined(def); }
    public T visit(StmtLet let) { return undefined(let); }

    public T undefined(ASTNode node) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot visit " + node);
    }
}
