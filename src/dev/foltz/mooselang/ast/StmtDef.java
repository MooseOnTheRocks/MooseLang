package dev.foltz.mooselang.ast;

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
    public String toString() {
        return "Def(" + name + ", " + params + ", " + body + ")";
    }
}
