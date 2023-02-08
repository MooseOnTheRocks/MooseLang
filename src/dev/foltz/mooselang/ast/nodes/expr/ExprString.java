package dev.foltz.mooselang.ast.nodes.expr;

import dev.foltz.mooselang.ast.VisitorAST;

public class ExprString extends ASTExpr {
    public final String value;

    public ExprString(String value) {
        this.value = value;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "String(\"" + value + "\")";
    }
}
