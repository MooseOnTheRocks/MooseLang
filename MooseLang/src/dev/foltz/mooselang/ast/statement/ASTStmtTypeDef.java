package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.typing.ASTType;

public class ASTStmtTypeDef implements ASTStmt {
    public final ASTExprName name;
    public final ASTType type;

    public ASTStmtTypeDef(ASTExprName name, ASTType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
