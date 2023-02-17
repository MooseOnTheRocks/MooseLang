package dev.foltz.mooselang.ast.nodes.type;

import dev.foltz.mooselang.ast.VisitorAST;

import java.util.List;

public class ASTTypeTuple extends ASTType {
    public final List<ASTType> types;

    public ASTTypeTuple(List<ASTType> types) {
        this.types = List.copyOf(types);
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTTypeTuple(" + types + ")";
    }
}
