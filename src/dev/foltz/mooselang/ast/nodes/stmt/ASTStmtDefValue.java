package dev.foltz.mooselang.ast.nodes.stmt;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ASTExprName;
import dev.foltz.mooselang.ast.nodes.type.ASTType;

import java.util.List;

public class ASTStmtDefValue extends ASTStmt {
    public final ASTExprName name;
    public final List<String> paramNames;
    public final List<ASTType> paramTypes;
    public final ASTExpr body;

    public ASTStmtDefValue(ASTExprName name, List<String> paramNames, List<ASTType> paramTypes, ASTExpr body) {
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
