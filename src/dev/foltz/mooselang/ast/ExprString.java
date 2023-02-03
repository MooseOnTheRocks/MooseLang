package dev.foltz.mooselang.ast;

public class ExprString extends ASTExpr {
    public final String value;

    public ExprString(String value) {
        this.value = value;
    }

    @Override
    public <T> T apply(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "String(\"" + value + "\")";
    }
}
