package dev.foltz.mooselang.interpreter.runtime;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.parser.ast.ASTVisitor;
import dev.foltz.mooselang.parser.ast.deconstructors.ASTDeconstructor;
import dev.foltz.mooselang.parser.ast.expressions.ASTExpr;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;

import java.util.ArrayList;
import java.util.List;

public class RTFuncDef extends RTFunc {
    public final String funcName;
    public final List<ASTDeconstructor> funcParams;
    public final ASTStmt funcBody;
    public final Scope externalScope;

    public RTFuncDef(String funcName, List<ASTDeconstructor> funcParams, ASTStmt funcBody, Scope externalScope) {
        this.funcName = funcName;
        this.funcParams = new ArrayList<>(funcParams);
        this.funcBody = funcBody;
        this.externalScope = externalScope;
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        int argsLen = args.size();
        int paramsLen = funcParams.size();

        if (argsLen != paramsLen) {
            return false;
        }

        for (int i = 0; i < paramsLen; i++) {
            RTObject arg = args.get(i);
            ASTDeconstructor decon = funcParams.get(i);

            if (!decon.matches(arg)) {
                return false;
            }
        }

        return true;
    }
}
