package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ASTDeconList extends ASTDeconstructor {
    public List<ASTDeconstructor> decons;

    public ASTDeconList(List<ASTDeconstructor> decons) {
        this.decons = new ArrayList<>(decons);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ASTDeconList{" +
                "decons=" + decons +
                '}';
    }
}
