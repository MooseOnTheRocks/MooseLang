package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.*;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class RTFuncBuiltinPrint extends RTFuncBuiltin {
    public RTFuncBuiltinPrint() {
        super("print");
    }

    @Override
    public RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args) {
        System.out.println(args.stream().map(this::rtObjectToString).collect(Collectors.joining(" ")));
        return RTNone.INSTANCE;
    }

    public String rtObjectToString(RTObject obj) {
        if (obj instanceof RTNone) {
            return "None";
        }
        else if (obj instanceof RTBool rtBool) {
            return rtBool.value ? "True" : "False";
        }
        else if (obj instanceof RTInt rtInt) {
            return "" + rtInt.value;
        }
        else if (obj instanceof RTList rtList) {
            return "["
                    + rtList.elems.stream()
                            .map(this::rtObjectToString)
                            .collect(Collectors.joining(", "))
                    + "]";
        }
        else if (obj instanceof RTString rtString) {
            return rtString.value;
        }
        else {
            return obj.toString();
        }
    }
}
