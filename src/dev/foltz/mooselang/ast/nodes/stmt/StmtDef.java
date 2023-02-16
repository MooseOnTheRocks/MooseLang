package dev.foltz.mooselang.ast.nodes.stmt;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ExprName;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.List;

public class StmtDef extends ASTStmt {
    public final ExprName name;
    public final List<String> paramNames;
    public final List<String> paramTypes;
    public final ASTExpr body;

    public StmtDef(ExprName name, List<String> paramNames, List<String> paramTypes, ASTExpr body) {
        this.name = name;
        this.paramNames = paramNames;
        this.paramTypes = paramTypes;
        this.body = body;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Def(" + name + ", " + paramNames + ", " + paramTypes + ", " + body + ")";
    }
}
