package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTList;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.interpreter.runtime.RTString;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RTFuncBuiltinConcat extends RTFuncBuiltin {
    public RTFuncBuiltinConcat() {
        super("concat");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        return new RTString(args.stream().map(rtObject -> ((RTString) rtObject).value).collect(Collectors.joining("")));
    }

    @Override
    public boolean accepts(List<RTObject> args) {
        return args.stream().allMatch(rtObject -> rtObject instanceof RTString);
    }
}
