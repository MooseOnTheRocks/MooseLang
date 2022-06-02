package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.typing.ASTType;

import java.util.Optional;

public class ASTStmtLet implements ASTStmt {
    public final ASTExprName name;
    public final Optional<ASTType> typeAnnotation;
    public final ASTExpr body;

    public ASTStmtLet(ASTExprName name, ASTType typeAnnotation, ASTExpr body) {
        this.name = name;
        this.typeAnnotation = Optional.ofNullable(typeAnnotation);
        this.body = body;
    }

    public ASTStmtLet(ASTExprName name, ASTExpr body) {
        this(name, null, body);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
