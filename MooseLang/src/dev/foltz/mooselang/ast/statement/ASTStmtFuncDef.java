package dev.foltz.mooselang.ast.statement;

import dev.foltz.mooselang.ast.ASTVisitor;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.ASTExprTyped;
import dev.foltz.mooselang.ast.typing.ASTType;

import java.util.List;
import java.util.Optional;

public class ASTStmtFuncDef implements ASTStmt {
    public final ASTExprName name;
    public final List<ASTExprTyped<ASTExprName>> typedParams;
    public final Optional<ASTType> retType;
    public final ASTExpr body;

    public ASTStmtFuncDef(ASTExprName name, List<ASTExprTyped<ASTExprName>> typedParams, ASTType retType, ASTExpr body) {
        this.name = name;
        this.typedParams = List.copyOf(typedParams);
        this.retType = Optional.ofNullable(retType);
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
