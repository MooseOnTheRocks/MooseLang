package dev.foltz.mooselang.interpreter.runtime;

import dev.foltz.mooselang.parser.ast.destructors.ASTDestructor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprBlock;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RTFuncDef extends RTFunc {
    public LinkedHashMap<List<ASTDestructor>, ASTExpr> branches;
    public RTFuncDef(String name) {
        super(name);
        branches = new LinkedHashMap<>();
    }

    public void addBranch(List<ASTDestructor> paramDtors, ASTExpr body) {
        branches.put(paramDtors, body);
    }
}
