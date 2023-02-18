package dev.foltz.mooselang.ast.nodes.stmt;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.expr.ASTExprName;
import dev.foltz.mooselang.ast.nodes.type.ASTType;

import java.util.List;

public class ASTStmtDefSumType extends ASTStmt {
    public final ASTExprName name;
    public final List<String> sumNames;
    public final List<List<ASTType>> sumParams;

    public ASTStmtDefSumType(ASTExprName name, List<String> sumNames, List<List<ASTType>> sumParams) {
        this.name = name;
        this.sumNames = List.copyOf(sumNames);
        this.sumParams = List.copyOf(sumParams);
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTStmtDefSumType(" + name + ", " + sumNames + ", " + sumParams + ")";
    }
}
