package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRDefType;
import dev.foltz.mooselang.ir.nodes.IRDefValue;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.IRCompBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.type.*;
import dev.foltz.mooselang.ir.nodes.type.tag.IRTypeTagName;
import dev.foltz.mooselang.ir.nodes.type.tag.IRTypeTagNumber;
import dev.foltz.mooselang.ir.nodes.value.*;

public abstract class VisitorIR<T> {
    // Top level structures
    public T visit(IRModule module) { return undefined(module); }
    public T visit(IRDefValue globalDef) { return undefined(globalDef); }
    public T visit(IRDefType defType) { return undefined(defType); }

    // IR Types
    public T visit(IRTypeUnit unit) { return undefined(unit); }
    public T visit(IRTypeName name) { return undefined(name); }
    public T visit(IRTypeNumber number) { return undefined(number); }
    public T visit(IRTypeString string) { return undefined(string); }
    public T visit(IRTypeTuple tuple) { return undefined(tuple); }
    public T visit(IRTypeSum sum) { return undefined(sum); }

    // Tags
    public T visit(IRTypeTagNumber tag) { return undefined(tag); }
    public T visit(IRTypeTagName tag) { return undefined(tag); }

    // IR Computations
    public T visit(IRCompBuiltin builtin) { return undefined(builtin); }
    public T visit(IRCompForce force) { return undefined(force); }
    public T visit(IRCompLambda lambda) { return undefined(lambda); }
    public T visit(IRCompCaseOf caseOf) { return undefined(caseOf); }
    public T visit(IRCompCaseOfBranch caseOfBranch) { return undefined(caseOfBranch); }
    public T visit(IRCompDo bind) { return undefined(bind); }
    public T visit(IRCompLet bind) { return undefined(bind); }
    public T visit(IRValueName name) { return undefined(name); }
    public T visit(IRCompProduce produce) { return undefined(produce); }
    public T visit(IRCompPush push) { return undefined(push); }

    // IR Values
    public T visit(IRValueFunctionHandle handle) { return undefined(handle); }
    public T visit(IRValueTagged tagged) { return undefined(tagged); }
    public T visit(IRValueAnnotated typed) { return undefined(typed); }
    public T visit(IRValueString string) { return undefined(string); }
    public T visit(IRValueThunk thunk) { return undefined(thunk); }
    public T visit(IRValueTuple tuple) { return undefined(tuple); }
    public T visit(IRValueUnit unit) { return undefined(unit); }
    public T visit(IRValueNumber number) { return undefined(number); }

    public T undefined(IRNode instruction) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot visit " + instruction);
    }
}
