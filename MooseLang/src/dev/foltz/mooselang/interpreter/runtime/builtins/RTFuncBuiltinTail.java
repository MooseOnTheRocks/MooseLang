package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RTFuncBuiltinTail extends RTFuncBuiltin {
    public RTFuncBuiltinTail() {
        super("tail");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        List<RTObject> tail = new ArrayList<>();
        RTList list = (RTList) args.get(0);
        for (int i = 0; i < list.elems.size(); i++) {
            if (i == 0) {
                continue;
            }
            tail.add(list.elems.get(i));
        }
        return new RTList(tail);
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.size() == 1 && args.get(0) instanceof RTList rtList && rtList.elems.size() >= 1;
    }
}
