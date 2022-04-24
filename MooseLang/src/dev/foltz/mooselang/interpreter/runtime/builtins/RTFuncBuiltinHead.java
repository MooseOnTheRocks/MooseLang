package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RTFuncBuiltinHead extends RTFuncBuiltin {
    public RTFuncBuiltinHead() {
        super("head");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        return ((RTList) args.get(0)).elems.get(0);
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.size() == 1 && args.get(0) instanceof RTList rtList && rtList.elems.size() >= 1;
    }
}
