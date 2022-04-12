package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtBind;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Interpreter {
    public final Map<String, RTObject> BUILTINS = Map.ofEntries(
            entry("print", new RTBuiltinPrint())
    );

    private final List<ASTStmt> remaining;
    private final Map<String, RTObject> nameBindings;

    public Interpreter() {
        remaining = new ArrayList<>();
        nameBindings = new HashMap<>();
        nameBindings.putAll(BUILTINS);
    }

    public boolean isEmpty() {
        return remaining.isEmpty();
    }

    public Interpreter feed(ASTStmt stmt) {
        this.remaining.add(stmt);
        return this;
    }

    public RTObject execExpr(ASTExpr expr) {
        if (expr instanceof ASTExprInt exprInt) {
            return new RTInt(exprInt.value);
        }
        else if (expr instanceof ASTExprName exprName) {
            return nameBindings.get(exprName.value);
        }
        else if (expr instanceof ASTExprList exprList) {
            List<RTObject> objects = new ArrayList<>();
            for (ASTExpr elem : exprList.elements) {
                objects.add(execExpr(elem));
            }
            return new RTList(objects);
        }
        else if (expr instanceof ASTExprCall exprCall) {
            String name = exprCall.name.value;
            RTObject obj = nameBindings.get(name);
            if (obj instanceof RTFunc func) {
                List<RTObject> params = new ArrayList<>();
                for (ASTExpr param : exprCall.params) {
                    params.add(execExpr(param));
                }
                return func.call(params);
            }
        }
        throw new IllegalStateException("Unable to execute expression: " + expr);
    }

    public void execBind(ASTStmtBind node) {
        String name = node.name.value;
        if (nameBindings.containsKey(name)) {
            throw new IllegalStateException("Name \"" + name + "\" already bound.");
        }
        RTObject obj = execExpr(node.expr);
        nameBindings.put(name, obj);
    }

    public void execStmt() {
        ASTStmt stmt = remaining.get(0);
        remaining.remove(0);
        if (stmt instanceof ASTStmtBind stmtBind) {
            execBind(stmtBind);
        }
        else if (stmt instanceof ASTStmtExpr stmtExpr) {
            execExpr(stmtExpr.expr);
        }
        else {
            throw new IllegalStateException("Could not execute bind: " + stmt);
        }
    }
}
