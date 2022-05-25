package dev.foltz.mooselang.ast.expression.literals;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;

import java.util.Map;

public class ASTExprRecord implements ASTExpr {
    public final Map<ASTExprName, ASTExpr> fields;

    public ASTExprRecord(Map<ASTExprName, ASTExpr> fields) {
        this.fields = Map.copyOf(fields);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
