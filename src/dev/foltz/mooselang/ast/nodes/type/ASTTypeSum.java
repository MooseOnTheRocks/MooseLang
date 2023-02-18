package dev.foltz.mooselang.ast.nodes.type;

import dev.foltz.mooselang.ast.VisitorAST;

import java.util.List;

public class ASTTypeSum extends ASTType {
    public final List<String> tagNames;
    public final List<List<ASTType>> tagParams;

    public ASTTypeSum(List<String> tagNames, List<List<ASTType>> tagParams) {
        this.tagNames = List.copyOf(tagNames);
        this.tagParams = List.copyOf(tagParams);
    }

    @Override
    public <T> T apply(VisitorAST<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTTypeSum(" + tagNames + ", " + tagParams + ")";
    }
}
