package dev.foltz.mooselang.ast.nodes.stmt;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ExprName;

import java.util.List;

public class StmtDef extends ASTStmt {
    public final ExprName name;
    public final List<ASTExpr> params;
    public final ASTExpr body;

    public StmtDef(ExprName name, List<ASTExpr> params, ASTExpr body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Def(" + name + ", " + params + ", " + body + ")";
    }
}
