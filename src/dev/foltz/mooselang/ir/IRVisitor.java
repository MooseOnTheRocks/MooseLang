package dev.foltz.mooselang.ir;

public abstract class IRVisitor<T> {
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
    public T visit(IRValue value) { return undefined(value); }

    public T undefined(IRNode instruction) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot visit " + instruction);
    }
}
