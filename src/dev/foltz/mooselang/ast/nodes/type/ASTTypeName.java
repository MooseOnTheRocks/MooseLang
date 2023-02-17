package dev.foltz.mooselang.ast.nodes.type;

import dev.foltz.mooselang.ast.VisitorAST;

public class ASTTypeName extends ASTType {
    public final String name;

    public ASTTypeName(String name) {
        this.name = name;
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTTypeName(" + name + ")";
    }
}
