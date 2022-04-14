package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.destructors.ASTDestInt;
import dev.foltz.mooselang.parser.ast.destructors.ASTDestName;
import dev.foltz.mooselang.parser.ast.destructors.ASTDestString;
import dev.foltz.mooselang.parser.ast.destructors.ASTDestructor;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtBind;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

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
                List<ASTExpr> params = node.params;
                List<ASTDestructor> paramDtors = new ArrayList<>();
                ASTExpr body = null;
                boolean match = false;
                for (Map.Entry<List<ASTDestructor>, ASTExpr> branch : rtDef.branches.entrySet()) {
                    match = true;
                    paramDtors = branch.getKey();
                    if (paramDtors.size() != params.size()) {
                        match = false;
                        continue;
                    }

                    for (int i = 0; i < paramDtors.size(); i++) {
                        ASTDestructor dtor = paramDtors.get(i);
                        ASTExpr param = params.get(i);
                        if (param instanceof ASTExprInt paramInt && dtor instanceof ASTDestInt dtorInt) {
                            if (paramInt.value != dtorInt.literal.value) {
                                match = false;
                                break;
                            }
                        }
                        else if (param instanceof ASTExprString paramStr && dtor instanceof ASTDestString dtorStr) {
                            if (!paramStr.value.equals(dtorStr.value.value)) {
                                match = false;
                                break;
                            }
                        }
                        else if (param instanceof ASTExprName paramName && dtor instanceof ASTDestName dtorName) {
                            if (!paramName.value.equals(dtorName.name.value)) {
                                match = false;
                                break;
                            }
                        }
                    }

                    body = branch.getValue();
                    if (match) {
                        break;
                    }
                }

                if (!match) {
                    throw new IllegalStateException("Could not call function " + name + " with: " + params);
                }

                System.out.println("Calling user defined function: " + name + "(" + params + ")");
                env.pushScope();
                for (int i = 0; i < paramDtors.size(); i++) {
                    ASTDestructor dtor = paramDtors.get(i);
                    if (dtor instanceof ASTDestName dtorName) {
                        String paramName = dtorName.name.value;
                        if (env.find(paramName) != null) {
                            throw new IllegalStateException("Attempt to bind already bound name " + paramName);
                        }
                        ASTExpr param = params.get(i);
                        env.bind(paramName, param.accept(this));
                    }
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
        RTObject binding = env.find(node.name.value);
        if (binding != null && !(binding instanceof RTFuncDef)) {
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
        String funcName = node.name.value;
        RTFuncDef funcDef;
        RTObject binding = env.find(funcName);
        if (binding instanceof RTFuncDef) {
            funcDef = (RTFuncDef) binding;
        }
        else {
            funcDef = new RTFuncDef(funcName);
            env.bind(funcName, funcDef);
        }
        funcDef.addBranch(node.paramDtors, node.body);
        return funcDef;
    }

    @Override
    public RTObject visit(ASTDestInt node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestInt");
    }

    @Override
    public RTObject visit(ASTDestName node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestName");
    }

    @Override
    public RTObject visit(ASTDestString node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestString");
    }
}
