package dev.foltz.mooselang.ast.typing;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExprName;

import java.util.Map;

public class ASTTypeRecord implements ASTType {
    public final Map<ASTExprName, ASTType> fields;

    public ASTTypeRecord(Map<ASTExprName, ASTType> fields) {
        this.fields = Map.copyOf(fields);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
