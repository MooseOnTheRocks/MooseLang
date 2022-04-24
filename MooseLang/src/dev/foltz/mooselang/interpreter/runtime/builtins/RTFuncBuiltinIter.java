package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.interpreter.runtime.RTString;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RTFuncBuiltinIter extends RTFuncBuiltin {
    public RTFuncBuiltinIter() {
        super("iter");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        List<RTObject> list;
        if (args.get(0) instanceof RTList rtList) {
            list = new ArrayList<>(rtList.elems);
        }
        else if (args.get(0) instanceof RTString rtString) {
            list = new ArrayList<>();
            for (char c : rtString.value.toCharArray()) {
                list.add(new RTString("" + c));
            }
        }
        else {
            throw new IllegalStateException("Cannot call iter with arguments: " + args);
        }
        return new RTList(list);
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.size() == 1 && args.get(0) instanceof RTList || args.get(0) instanceof RTString;
    }
}
