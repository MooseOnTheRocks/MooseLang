package dev.foltz.mooselang.ast.typing;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;

public class ASTTypeLiteral implements ASTType {
    public ASTExpr literal;

    public ASTTypeLiteral(ASTExpr literal) {
        this.literal = literal;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
