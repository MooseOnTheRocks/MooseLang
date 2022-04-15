package dev.foltz.mooselang.parser.ast.expressions;

import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;

import java.util.List;

public class ASTExprFuncDef extends ASTExpr {
    public final ASTExprName name;
    public final List<ASTDeconstructor> paramDtors;
    public final ASTExpr body;

    public ASTExprFuncDef(ASTExprName name, List<ASTDeconstructor> params, ASTExpr body) {
        this.name = name;
        this.paramDtors = params;
        this.body = body;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTExprFuncDef{" +
                "name=" + name +
                ", params=" + paramDtors +
                ", body=" + body +
                '}';
    }
}
