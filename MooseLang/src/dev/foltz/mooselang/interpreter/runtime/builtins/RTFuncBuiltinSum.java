package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTInt;
import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class RTFuncBuiltinSum extends RTFuncBuiltin {
    public RTFuncBuiltinSum() {
        super("sum");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        return new RTInt(args.stream().map(arg -> ((RTInt) arg).value).reduce(0, Integer::sum));
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.stream().allMatch(rtObject -> rtObject instanceof RTInt);
    }
}
