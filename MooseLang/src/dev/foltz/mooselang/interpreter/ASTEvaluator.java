package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.ASTNode;

import java.util.function.Function;

public class ASTEvaluator extends ASTDefaultVisitor<Object> {

    public ASTEvaluator() {
        super(node -> { throw new UnsupportedOperationException("Cannot evaluate node: " + node); });
    }

}
