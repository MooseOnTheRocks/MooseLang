package dev.foltz.mooselang.interpreter.runtime.builtins;

import dev.foltz.mooselang.interpreter.runtime.RTFunc;
import dev.foltz.mooselang.interpreter.runtime.RTFuncDef;
import dev.foltz.mooselang.interpreter.runtime.RTFuncDispatcher;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTVisitor;

import java.util.List;
import java.util.Optional;

public abstract class RTFuncBuiltin extends RTFunc {
    public final String name;

    public RTFuncBuiltin(String name) {
        this.name = name;
    }

    public RTFuncDispatcher createDispatcher() {
        return new RTFuncDispatcher(name) {
            @Override
            public Optional<RTFunc> dispatch(List<RTObject> args) {
                return Optional.of(RTFuncBuiltin.this);
            }
        };
    }

    public abstract RTObject call(ASTVisitor<RTObject> interp, List<RTObject> args);

    @Override
    public boolean accepts(List<RTObject> args) {
        return true;
    }
}
