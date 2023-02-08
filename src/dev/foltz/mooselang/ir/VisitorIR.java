package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.IRName;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.ir.nodes.value.IRString;
import dev.foltz.mooselang.ir.nodes.value.IRUnit;

public abstract class VisitorIR<T> {
    public T visit(IRBuiltin builtin) { return undefined(builtin); }
    public T visit(IRForceName force) { return undefined(force); }
    public T visit(IRForceThunk force) { return undefined(force); }
    public T visit(IRLambda lambda) { return undefined(lambda); }
    public T visit(IRLetComp bind) { return undefined(bind); }
    public T visit(IRLetValue bind) { return undefined(bind); }
    public T visit(IRName name) { return undefined(name); }
    public T visit(IRProduce produce) { return undefined(produce); }
    public T visit(IRPush push) { return undefined(push); }
    public T visit(IRString string) { return undefined(string); }
    public T visit(IRThunk thunk) { return undefined(thunk); }
    public T visit(IRUnit unit) { return undefined(unit); }
    public T visit(IRNumber number) { return undefined(number); }

    public T undefined(IRNode instruction) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot visit " + instruction);
    }
}
