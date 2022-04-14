package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

import java.util.ArrayList;
import java.util.List;

public class ASTExprBlock extends ASTExpr {
    public List<ASTStmt> stmts;

    public ASTExprBlock(List<ASTStmt> stmts) {
        this.stmts = List.copyOf(stmts);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprBlock{...}";
    }
}
