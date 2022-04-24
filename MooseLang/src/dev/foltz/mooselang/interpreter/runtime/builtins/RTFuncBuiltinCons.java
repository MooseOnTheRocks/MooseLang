package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RTFuncBuiltinCons extends RTFuncBuiltin {
    public RTFuncBuiltinCons() {
        super("cons");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        List<RTObject> list = new ArrayList<>();
        list.add(args.get(0));
        list.addAll(((RTList) args.get(1)).elems);
        return new RTList(list);
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.size() == 2 && args.get(1) instanceof RTList rtList;
    }
}
