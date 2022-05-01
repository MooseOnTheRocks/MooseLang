package dev.foltz.mooselang.interpreter.rt;

import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.interpreter.RTVisitor;

import java.util.List;

public class RTFuncDef extends RTObject {
    public final List<String> paramNames;
    public final ASTStmt body;

    public RTFuncDef(List<String> paramNames, ASTStmt body) {
        this.paramNames = paramNames;
        this.body = body;
    }

    @Override
    public <T> T accept(RTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
