package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.interpreter.runtime.RTValue;

public class ASTEvaluator extends ASTDefaultVisitor<RTValue> {
    public final Env<RTValue> scope;

    public ASTEvaluator(Env<RTValue> scope) {
        super(node -> { throw new UnsupportedOperationException("Cannot evaluate node: " + node); });
        this.scope = scope.copy();
    }

    public ASTEvaluator() {
        this(new Env<>());
    }

    public ASTEvaluator with(String name, RTValue value) {
        if (scope.contains(name)) {
            String msg = "ASTEvaluator.with: cannot bind already bound name in scope: " + name + " = " + value + "\n"
                + "Previously bound: " + name + " = " + scope.find(name).get();
            throw new IllegalStateException(msg);
        }

        return new ASTEvaluator(scope.with(name, value));
    }

    @Override
    public RTValue visit(ASTExprInt node) {
        return new RTInt(node.value());
    }
}
