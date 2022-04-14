package dev.foltz.mooselang.interpreter.runtime;

import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprBlock;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

import java.util.ArrayList;
import java.util.List;

public class RTFuncDef extends RTFunc {
    public ASTExpr body;
    public List<String> paramNames;
    public RTFuncDef(String name, List<String> paramNames, ASTExpr body) {
        super(name);
        this.paramNames = new ArrayList<>(paramNames);
        this.body = body;
    }
}
