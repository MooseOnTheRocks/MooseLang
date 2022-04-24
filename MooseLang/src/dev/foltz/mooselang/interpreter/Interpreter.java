package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncBuiltin;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.*;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.statements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter implements ASTVisitor<RTObject> {
    private final List<ASTStmt> remaining;
    public Scope env;

    public Interpreter(Map<String, RTObject> globals) {
        this.env = new Scope();
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
        RTObject res = stmt.accept(this);
        return res;
    }

    @Override
    public RTObject visit(ASTExprNone node) {
        return RTNone.INSTANCE;
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
    public RTObject visit(ASTExprBool node) {
        return new RTBool(node.value);
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
        RTObject binding = env.findAnyScope(node.value);
        if (binding == null) {
            throw new IllegalStateException("Cannot find name " + node.value);
        }
        return binding;
    }

    @Override
    public RTObject visit(ASTExprIfThenElse node) {
        RTObject evalCond = node.exprCond.accept(this);
        if (evalCond instanceof RTBool rtBool) {
            if (rtBool.value) {
                return node.exprTrue.accept(this);
            }
            else {
                return node.exprFalse.accept(this);
            }
        }
        else {
            throw new IllegalStateException("IfThenElse expects boolean result for condition, received: " + evalCond);
        }
    }

    @Override
    public RTObject visit(ASTExprCall node) {
        RTObject binding = env.findAnyScope(node.name.value);
//        System.out.println("Calling: " + node.name + " => " + binding);

        if (binding instanceof RTFuncDispatcher dispatcher) {
            List<RTObject> evalParams = node.params.stream().map(param -> param.accept(this)).toList();
//            System.out.println("With: " + evalParams);
            RTFunc rtFunc = dispatcher.dispatch(evalParams).orElseThrow();
            if (rtFunc.accepts(evalParams)) {
                RTObject result;
                if (rtFunc instanceof RTFuncBuiltin builtinFunc) {
                    result = builtinFunc.call(this, evalParams);
                }
                else if (rtFunc instanceof RTFuncDef funcDef) {
                    Scope saveEnv = env;
                    env = funcDef.externalScope;
                    for (int i = 0; i < evalParams.size(); i++) {
                        RTObject arg = evalParams.get(i);
                        ASTDeconstructor decon = funcDef.funcParams.get(i);
                        decon.deconstruct(arg, env);
                    }
                    result = funcDef.funcBody.accept(this);
                    env = saveEnv;
                }
                else {
                    throw new IllegalStateException("Cannot dispatch function on " + node.name.value + " with arguments: " + evalParams);
                }
                return result;
            }
        }

        throw new IllegalStateException("Call failed on object: " + node);
    }

    @Override
    public RTObject visit(ASTExprLambda node) {
        RTFuncDef funcDef = new RTFuncDef("<lambda>", node.paramDtors, node.body, new Scope(env));
        RTFuncDispatcher dispatcher = new RTFuncDispatcher("<lambda>");
        dispatcher.addFuncDef(funcDef);
        return dispatcher;
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
    public RTObject visit(ASTExprAssign node) {
        RTObject binding = env.findAnyScope(node.name.value);
        if (binding == null) {
            throw new IllegalStateException("Cannot reassign " + node.name + " without previous definition");
        }
        RTObject value = node.expr.accept(this);
        env.reassign(node.name.value, value);
        return value;
    }

    @Override
    public RTObject visit(ASTExprNegate node) {
        RTObject evalExpr = node.expr.accept(this);
        if (evalExpr instanceof RTInt rtInt) {
            return new RTInt(-rtInt.value);
        }
        throw new IllegalStateException("Cannot negate non-int object: " + evalExpr);
    }

    @Override
    public RTObject visit(ASTStmtFuncDef node) {
        String funcName = node.name.value;
        RTObject binding = env.findAnyScope(funcName);
        RTFuncDispatcher funcDispatcher;
        if (binding instanceof RTFuncDispatcher rtDispatcher) {
            funcDispatcher = rtDispatcher;
        }
        else {
            funcDispatcher = new RTFuncDispatcher(node.name.value);
            env.bind(funcName, funcDispatcher);
        }


        funcDispatcher.addFuncDef(new RTFuncDef(funcName, node.paramDtors, node.body, new Scope(this.env)));
        return funcDispatcher;
    }

    @Override
    public RTObject visit(ASTStmtLet node) {
        RTObject binding = env.findInScope(node.name.value);
        if (binding != null) {
            throw new IllegalStateException("Name \"" + node.name.value + "\" already defined in scope.");
        }
        RTObject value = node.expr.accept(this);
        env.bind(node.name.value, value);
        return value;
    }

    @Override
    public RTObject visit(ASTExprLetIn node) {
        RTObject binding = env.findInScope(node.name.value);
        if (binding != null) {
            throw new IllegalStateException("Name \"" + node.name.value + "\" already defined in scope.");
        }
        RTObject value = node.expr.accept(this);
        env.pushScope();
        env.bind(node.name.value, value);
        RTObject result = node.body.accept(this);
        env.popScope();
        return result;
    }

    @Override
    public RTObject visit(ASTStmtExpr node) {
        return node.expr.accept(this);
    }

    @Override
    public RTObject visit(ASTExprForInThenElse node) {
        ASTDeconstructor decon = node.variableDecon;
        RTObject listExpr = node.listExpr.accept(this);
        List<RTObject> elems;
        if (listExpr instanceof RTList rtList) {
            elems = rtList.elems;
        }
        else {
            throw new IllegalStateException("For-in loop iterator expression must evaluate to a list, instead got: " + listExpr);
        }

        RTObject lastResult = null;
        if (decon instanceof ASTDeconName deconName) {
            String name = deconName.name.value;
            for (RTObject elem : elems) {
                env.pushScope();
                env.bind(name, elem);
                lastResult = node.bodyLoop.accept(this);
                env.popScope();
            }
        }
        else {
            throw new IllegalStateException("For-in loop variable deconstructor must be name (for now), instead got: " + decon);
        }

        return lastResult == null ? node.bodyElse.accept(this) : lastResult;
    }

    @Override
    public RTObject visit(ASTStmtForInDo node) {
        ASTDeconstructor decon = node.variableDecon;
        RTObject listExpr = node.listExpr.accept(this);
        List<RTObject> elems = new ArrayList<>();
        if (listExpr instanceof RTList rtList) {
            elems = rtList.elems;
        }
        else {
            throw new IllegalStateException("For-in loop iterator expression must evaluate to a list, instead got: " + listExpr);
        }

        if (decon instanceof ASTDeconName deconName) {
            String name = deconName.name.value;
            for (RTObject elem : elems) {
                env.pushScope();
                env.bind(name, elem);
                node.body.accept(this);
                env.popScope();
            }
        }
        else {
            throw new IllegalStateException("For-in loop variable deconstructor must be name (for now), instead got: " + decon);
        }

        return RTNone.INSTANCE;
    }

    @Override
    public RTObject visit(ASTStmtIfDo node) {
        RTObject evalCond = node.exprCond.accept(this);
        if (evalCond instanceof RTBool rtBool) {
            if (rtBool.value) {
                node.exprTrue.accept(this);
            }
        }
        else {
            throw new IllegalStateException("If-do statement requires boolean conditional, received: " + evalCond);
        }

        return RTNone.INSTANCE;
    }

    @Override
    public RTObject visit(ASTDeconInt node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconInt");
    }

    @Override
    public RTObject visit(ASTDeconName node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconName");
    }

    @Override
    public RTObject visit(ASTDeconString node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconString");
    }

    @Override
    public RTObject visit(ASTDeconChar node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconChar");
    }

    @Override
    public RTObject visit(ASTDeconList node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconList");
    }
}
