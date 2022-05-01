package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprBlock;
import dev.foltz.mooselang.ast.expression.ASTExprCall;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.ast.statement.ASTStmtExpr;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.interpreter.rt.RTBuiltinFunc;
import dev.foltz.mooselang.interpreter.rt.RTFuncDef;
import dev.foltz.mooselang.interpreter.rt.RTInt;
import dev.foltz.mooselang.interpreter.rt.RTObject;

import java.util.List;
import java.util.stream.Collectors;

public class ASTEvaluator extends ASTDefaultVisitor<RTObject> {
    public final IScope globals;
    public final IScope scope;

    public ASTEvaluator(IScope globals) {
        super(node -> { throw new UnsupportedOperationException("Cannot evaluate node: " + node); });
        this.globals = new FlatScope(globals);
        this.scope = new FlatScope(globals);
    }

    public static RTObject eval(ASTNode node, IScope scope) {
        ASTEvaluator evaluator = new ASTEvaluator(scope);
        return node.accept(evaluator);
    }

    @Override
    public RTObject visit(ASTStmtLet node) {
        String name = node.name().value();
        if (scope.contains(name)) {
            throw new IllegalStateException("Cannot bind already bound name in scope: " + name);
        }
        RTObject rtObj = node.expr().accept(this);
        scope.bind(name, rtObj);
        return null;
    }

    @Override
    public RTObject visit(ASTExprName node) {
        String name = node.value();
        if (!scope.contains(name)) {
            throw new IllegalStateException("Cannot find name in scope: " + name);
        }
        return scope.find(name).boundObject();
    }

    @Override
    public RTObject visit(ASTExprInt node) {
        int value = node.value();
        return new RTInt(value);
    }

    @Override
    public RTObject visit(ASTStmtExpr node) {
        return node.expr().accept(this);
    }

    @Override
    public RTObject visit(ASTExprBlock node) {
        RTObject lastEval = null;
        for (ASTStmt stmt : node.stmts()) {
            lastEval = stmt.accept(this);
        }
        return lastEval;
    }

    @Override
    public RTObject visit(ASTStmtFuncDef node) {
        String name = node.name().value();
        if (scope.contains(name)) {
            throw new IllegalStateException("Cannot define previously defined function: " + name);
        }
        List<String> params = node.params().stream().map(ASTExprName::value).toList();
        ASTStmt body = node.body();

        RTFuncDef funcDef = new RTFuncDef(params, body);
        scope.bind(name, funcDef);
        return funcDef;
    }

    @Override
    public RTObject visit(ASTExprCall node) {
        String name = node.name().value();
        if (!scope.contains(name)) {
            throw new IllegalStateException("Cannot find name in scope: " + name);
        }
        NameBinding nameBinding = scope.find(name);
        RTObject boundObject = nameBinding.boundObject();
        if (boundObject instanceof RTFuncDef funcDef) {
            List<ASTExpr> params = node.params();
            if (params.size() != funcDef.paramNames.size()) {
                throw new IllegalStateException("Function " + name + " expects " + funcDef.paramNames.size() + " parameters, received " + params.size());
            }
            List<RTObject> evalParams = params.stream().map(expr -> expr.accept(this)).toList();
            System.out.println("Calling " + name + "(" + evalParams + ")");
            IScope funcScope = new FlatScope(this.globals);
            for (int i = 0; i < evalParams.size(); i++) {
                RTObject paramObj = evalParams.get(i);
                String paramName = funcDef.paramNames.get(i);
                funcScope.bind(paramName, paramObj);
            }
            return eval(funcDef.body, funcScope);
        }
        else if (boundObject instanceof RTBuiltinFunc funcBuiltin) {
            List<RTObject> evalParams = node.params().stream().map(expr -> expr.accept(this)).toList();
            return funcBuiltin.call(evalParams);
        }
        else {
            throw new IllegalStateException("Cannot call bound object: " + nameBinding);
        }

    }
}
