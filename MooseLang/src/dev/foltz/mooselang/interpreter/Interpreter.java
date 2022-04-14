package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtBind;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter implements ASTVisitor<RTObject> {
    private final List<ASTStmt> remaining;
    private final Env env;

    public Interpreter(Map<String, RTObject> globals) {
        this.env = new Env();
        for (Map.Entry<String, RTObject> entry : globals.entrySet()) {
            env.bind(entry.getKey(), entry.getValue());
        }
        this.remaining = new ArrayList<>();
    }

    public void feed(ASTStmt stmt) {
        remaining.add(stmt);
    }

    public boolean isEmpty() {
        return remaining.isEmpty();
    }

    public RTObject execNext() {
        ASTStmt stmt = remaining.remove(0);
        return stmt.accept(this);
    }

    @Override
    public RTObject visit(ASTExprInt node) {
        return new RTInt(node.value);
    }

    @Override
    public RTObject visit(ASTExprString node) {
        return new RTString(node.value);
    }

    @Override
    public RTObject visit(ASTExprList node) {
        List<RTObject> elems = new ArrayList<>();
        for (ASTExpr elem : node.elements) {
            elems.add(elem.accept(this));
        }
        return new RTList(elems);
    }

    @Override
    public RTObject visit(ASTExprName node) {
        return env.find(node.value);
    }

    @Override
    public RTObject visit(ASTExprCall node) {
        RTObject binding = env.find(node.name.value);
        if (binding instanceof RTFunc rtFunc) {
            // Builtin function application
            if (rtFunc.name.equals("print")) {
                StringBuilder sb = new StringBuilder();
                sb.append(node.params.stream()
                        .map(param -> param.accept(this))
                        .map(RTFuncPrint::print)
                        .collect(Collectors.joining(" ")));
                System.out.println(sb);
                return RTNone.INSTANCE;
            }
            // User defined function application
            else if (rtFunc instanceof RTFuncDef rtDef) {
                String name = rtDef.name;
                ASTExpr body = rtDef.body;
                List<String> paramNames = rtDef.paramNames;
                List<ASTExpr> params = node.params;
                if (paramNames.size() != params.size()) {
                    throw new IllegalStateException("Function " + name + " called with " + params.size() + " parameters, expects " + paramNames.size());
                }
//                System.out.println("Calling user defined function: " + name + "(" + params + ")");
                env.pushScope();
                for (int i = 0; i < paramNames.size(); i++) {
                    String paramName = paramNames.get(i);
                    if (env.find(paramName) != null) {
                        throw new IllegalStateException("Attempt to bind already bound name " + paramName);
                    }
                    ASTExpr param = params.get(i);
                    env.bind(paramName, param.accept(this));
                }
                RTObject result = body.accept(this);
                env.popScope();
                return result;
            }
        }
        throw new UnsupportedOperationException("Unrecognized function in call: " + node.name.value);
    }

    @Override
    public RTObject visit(ASTExprBlock node) {
        env.pushScope();
        RTObject lastObj = RTNone.INSTANCE;
        for (ASTStmt stmt : node.stmts) {
            lastObj = stmt.accept(this);
        }
        env.popScope();
        return lastObj;
    }

    @Override
    public RTObject visit(ASTStmtBind node) {
        if (env.find(node.name.value) != null) {
            throw new IllegalStateException("Name \"" + node.name.value + "\" already bound.");
        }
        env.bind(node.name.value, node.expr.accept(this));
        return RTNone.INSTANCE;
    }

    @Override
    public RTObject visit(ASTStmtExpr node) {
        return node.expr.accept(this);
    }

    @Override
    public RTObject visit(ASTExprFuncDef node) {
        List<String> paramNames = node.paramNames.stream().map(pn -> pn.value).collect(Collectors.toList());
        return new RTFuncDef(node.name.value, paramNames, node.body);
    }
}
