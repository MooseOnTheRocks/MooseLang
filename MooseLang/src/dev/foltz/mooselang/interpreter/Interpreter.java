package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncCons;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncHead;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncPrint;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncTail;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprList;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprNone;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtAssign;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter implements ASTVisitor<RTObject> {
    private final List<ASTStmt> remaining;
    public final Env env;

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
        RTObject res = stmt.accept(this);
        return res;
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
        RTObject binding = env.find(node.value);
        if (binding == null) {
            throw new IllegalStateException("Cannot find name " + node.value);
        }
        return binding;
    }

    @Override
    public RTObject visit(ASTExprCall node) {
        RTObject binding = env.find(node.name.value);
        List<ASTExpr> params = node.params;
        List<RTObject> evalParams = params.stream().map(param -> param.accept(this)).toList();
        if (binding instanceof RTFunc rtFunc) {
            if (rtFunc instanceof RTFuncPrint rtFuncPrint) {
                StringBuilder sb = new StringBuilder();
                sb.append(evalParams.stream()
                        .map(RTFuncPrint::print)
                        .collect(Collectors.joining(" ")));
                System.out.println(sb);
                return RTNone.INSTANCE;
            }
            else if (rtFunc instanceof RTFuncHead) {
                if (evalParams.size() != 1) {
                    throw new IllegalStateException("head expects 1 argument, received " + evalParams.size());
                }

                RTObject param = evalParams.get(0);
                if (param instanceof RTList rtList) {
                    if (rtList.elems.size() == 0) {
                        throw new IllegalStateException("Cannot call head on empty list");
                    }
                    return rtList.elems.get(0);
                }

                throw new IllegalStateException("head expects list, received: " + param);
            }
            else if (rtFunc instanceof RTFuncTail) {
                if (evalParams.size() != 1) {
                    throw new IllegalStateException("tail expects 1 argument, received " + evalParams.size());
                }

                RTObject param = evalParams.get(0);
                if (param instanceof RTList rtList) {
                    if (rtList.elems.size() == 0) {
                        return new RTList(new ArrayList<>());
                    }

                    List<RTObject> objs = rtList.elems.subList(1, rtList.elems.size());
                    return new RTList(objs);
                }

                throw new IllegalStateException("tail expects list, received: " + param);
            }
            else if (rtFunc instanceof RTFuncCons) {
                if (evalParams.size() != 2) {
                    throw new IllegalStateException("cons expects 2 arguments, received " + evalParams.size());
                }

                RTObject elem = evalParams.get(0);
                RTObject param2 = evalParams.get(1);

                if (param2 instanceof RTList rtList) {
                    List<RTObject> elems = rtList.elems;
                    elems.add(0, elem);
                    return new RTList(elems);
                }
                else {
                    throw new IllegalStateException("cons expects list for 2nd argument, received " + param2);
                }
            }
            // User defined function application
            else {
                String name = rtFunc.name;
//                List<RTObject> evalParams = params.stream().map(param -> param.accept(this)).toList();
                RTFunc.RTFuncBranch branch = rtFunc.dispatch(evalParams);
                List<ASTDeconstructor> branchDecons = branch.paramDecons;

                // System.out.println("Calling user defined function: " + name + "(" + params + ")");
                env.pushScope();
                for (int i = 0; i < branchDecons.size(); i++) {
                    ASTDeconstructor decon = branchDecons.get(i);
                    if (decon instanceof ASTDeconName deconName) {
                        String paramName = deconName.name.value;
//                        if (env.find(paramName) != null) {
//                            throw new IllegalStateException("Attempt to bind already bound name " + paramName);
//                        }
                        ASTExpr param = params.get(i);
                        env.bind(paramName, param.accept(this));
                    }
                }
                RTObject result = branch.body.accept(this);
                env.popScope();
                return result;
            }
        }

        throw new IllegalStateException("Call failed on object: " + node);
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
    public RTObject visit(ASTStmtAssign node) {
        RTObject binding = env.find(node.name.value);
//        if (binding != null) {
//            throw new IllegalStateException("Name \"" + node.name.value + "\" already bound.");
//        }
        RTObject value = node.expr.accept(this);
        env.bind(node.name.value, value);
        return value;
    }

    @Override
    public RTObject visit(ASTStmtExpr node) {
        return node.expr.accept(this);
    }

    @Override
    public RTObject visit(ASTExprFuncDef node) {
        String funcName = node.name.value;
        RTObject binding = env.find(funcName);
        RTFunc funcDef;
        if (binding instanceof RTFunc rtFunc) {
            funcDef = rtFunc;
        }
        else {
            funcDef = new RTFunc(funcName);
            env.bind(funcName, funcDef);
        }
        funcDef.addBranch(new RTFunc.RTFuncBranch(node.paramDtors, node.body));
        return funcDef;
    }

    @Override
    public RTObject visit(ASTExprNone node) {
        return RTNone.INSTANCE;
    }

    @Override
    public RTObject visit(ASTDeconInt node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestInt");
    }

    @Override
    public RTObject visit(ASTDeconName node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestName");
    }

    @Override
    public RTObject visit(ASTDeconString node) {
        throw new UnsupportedOperationException("Cannot visit ASTDestString");
    }

    @Override
    public RTObject visit(ASTDeconList node) {
        throw new UnsupportedOperationException("Cannot visit ASTDeconList");
    }
}
