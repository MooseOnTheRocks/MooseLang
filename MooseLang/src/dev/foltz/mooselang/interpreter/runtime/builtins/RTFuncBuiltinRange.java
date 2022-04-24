package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTInt;
import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RTFuncBuiltinRange extends RTFuncBuiltin {
    public RTFuncBuiltinRange() {
        super("range");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        int a, b;
        if (args.size() == 1) {
            a = 0;
            b = ((RTInt) args.get(0)).value;
        }
        else {
            a = ((RTInt) args.get(0)).value;
            b = ((RTInt) args.get(1)).value;
        }

        List<RTObject> list = new ArrayList<>();
        for (int i = a; i < b; i++) {
            list.add(new RTInt(i));
        }

        return new RTList(list);
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return switch (args.size()) {
            case 1 -> args.get(0) instanceof RTInt;
            case 2 -> args.get(0) instanceof RTInt && args.get(1) instanceof RTInt;
            default -> false;
        };
    }
}
