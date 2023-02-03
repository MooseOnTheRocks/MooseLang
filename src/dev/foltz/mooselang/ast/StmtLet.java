package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.parser.SourceDesc;

public class StmtLet extends ASTStmt {
    public final ExprName name;
    public final ASTExpr expr;

    public StmtLet(ExprName name, ASTExpr expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Let(" + name + ", " + expr + ")";
    }
}
