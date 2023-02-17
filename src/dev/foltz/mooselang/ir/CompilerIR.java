package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtDef;
import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeName;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeTuple;
import dev.foltz.mooselang.ir.nodes.IRGlobalDef;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.types.IRTypeName;
import dev.foltz.mooselang.ir.nodes.types.IRTypeTuple;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.value.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompilerIR extends VisitorAST<IRNode> {
    // TODO: Move this somewhere else.
    public TypeValue getType(ASTType type) {
        if (type instanceof ASTTypeName name) {
            return switch (name.name) {
                case "Number" -> new ValueNumber();
                case "String" -> new ValueString();
                case "()" -> new ValueUnit();
                default -> throw new RuntimeException("getType of unknown type: " + name.name);
            };
        }
        else if (type instanceof ASTTypeTuple tuple) {
            return new ValueTuple(tuple.types.stream().map(this::getType).toList());
        }
        throw new RuntimeException("getType of unknown type: " + type);
    }

    public static IRNode compile(ASTNode node) {
        return new CompilerIR().compileNode(node);
    }

    public IRNode compileNode(ASTNode node) {
        return node.apply(this);
    }

    private static IRValueThunk curry(IRComp body, List<String> paramNames, List<TypeValue> paramTypes) {
        if (paramNames.size() != paramTypes.size()) {
            throw new RuntimeException("curry with different sized name and type lists: " + paramNames + ", " + paramTypes);
        }

        // U(A -> F(...))
        // F(InnerType)
        IRValueThunk wrapped = null;
        int apps = 0;
//        var paramEntries = new ArrayList<>(typedParams.entrySet().stream().toList());
//        Collections.reverse(paramEntries);
        for (int i = paramNames.size() - 1; i >= 0; i--) {
            var name = paramNames.get(i);
            var type = paramTypes.get(i);
            if (apps == 0) {
                wrapped = new IRValueThunk(new IRCompLambda(name, type, body), Map.of());
            }
            else {
                wrapped = new IRValueThunk(new IRCompLambda(name, type, new IRCompProduce(wrapped)), Map.of());
            }
            apps += 1;
        }
        return wrapped;
    }

    @Override
    public IRNode visit(ASTStmtDef def) {
        var body = compileNode(def.body);
        if (body instanceof IRComp bodyComp) {
            return new IRGlobalDef(def.name.name, curry(bodyComp, def.paramNames, def.paramTypes.stream().map(this::getType).toList()));
        }
        return error("StmtDef expects body of computation.");
    }

    @Override
    public IRNode visit(ASTExprTypeAnnotated annotated) {
        var value = compileNode(annotated.expr);
//        var type = compileNode(annotated.type);
        if (value instanceof IRValue irValue) {
            return irValue;
        }
        return error("Invalid type annotation: " + value + " :: " + annotated.type);
    }

    @Override
    public IRNode visit(ASTExprChain chain) {
        var lhs = compileNode(chain.first);
        var rhs = compileNode(chain.second);

        if (lhs instanceof IRComp lhsComp && rhs instanceof IRComp rhsComp) {
            return new IRCompDo("_", lhsComp, rhsComp);
        }
        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRValue rhsValue) {
            return new IRCompDo("_", lhsComp, new IRCompProduce(rhsValue));
        }
        return error("Cannot chain:\nlhs: " + lhs + "\nrhs: " + rhs);
    }

    static int args = 0;
    @Override
    public IRNode visit(ASTExprApply apply) {
        var lhs = compileNode(apply.lhs);
        var rhs = compileNode(apply.rhs);

        if (lhs instanceof IRValueName lhsName && rhs instanceof IRValue rhsValue) {
            return new IRCompPush(rhsValue, new IRCompForce(lhsName));
        }
        else if (lhs instanceof IRValueName lhsName && rhs instanceof IRComp rhsComp) {
            var argname = "_app_" + (args++);
            return new IRCompDo(argname, rhsComp, new IRCompPush(new IRValueName(argname), new IRCompForce(lhsName)));
        }
//        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRName rhsName) {
//            var argname = "_app_" + (args++);
//            return new IRDo(argname, lhsComp, new IRPush(new IRName(argname), new IRForce(rhsName)));
//        }
        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRValue rhsValue) {
            var argname = "_app_" + (args++);
            return new IRCompDo(argname, lhsComp, new IRCompPush(rhsValue, new IRCompForce(new IRValueName(argname))));
        }
        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRComp rhsComp) {
            var lhsName = "_app_" + (args++);
            var rhsName = "_app_" + (args++);
            return new IRCompDo(lhsName, lhsComp, new IRCompDo(rhsName, rhsComp, new IRCompPush(new IRValueName(rhsName), new IRCompForce(new IRValueName(lhsName)))));
        }

        // Assume this is function application so name corresponds to (A -> B)
//        if (lhs instanceof IRName name && rhs instanceof IRComp rhsComp) {
//            var argname = "__arg_" + (args++);
//            return new IRDo(argname, rhsComp, new IRPush(new IRName(argname), new IRForce(name)));
//        }
//        else if (lhs instanceof IRName name && rhs instanceof IRValue rhsValue) {
//                return new IRPush(rhsValue, new IRForce(name));
//        }
//        else if (lhs instanceof IRValue lhsValue && rhs instanceof IRName name) {
//            return new IRPush(lhsValue, new IRForce(name));
//        }
//        else if (lhs instanceof IRLambda lhsLambda && rhs instanceof IRComp rhsComp) {
//            var argname = "__app_" + (args++);
//            return new IRDo(argname, rhsComp, new IRPush(new IRName(argname), lhsLambda));
//        }
//        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRComp rhsComp) {
//            var rhsName = "__arg_" + (args++);
//            var lhsName = "__app_" + (args++);
//            return new IRDo(rhsName, rhsComp, new IRDo(lhsName, lhsComp, new IRPush(new IRName(rhsName), new IRForce(new IRName(lhsName)))));
//        }
//        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRValue rhsValue) {
//            var argname = "__app_" + (args++);
//            return new IRDo(argname, lhsComp, new IRPush(rhsValue, new IRForce(new IRName(argname))));
//        }
//        else if (lhs instanceof IRValue lhsValue && rhs instanceof IRLambda rhsLambda) {
//            return new IRPush(lhsValue, rhsLambda);
//        }
//        else if (lhs instanceof IRValue lhsValue && rhs instanceof IRName rhsName) {
//            return new IRPush(lhsValue, new IRForce(rhsName));
//        }
//        else {
            return error("Application failed:\nlhs: " + lhs + "\nrhs: " + rhs);
//        }
    }

    @Override
    public IRNode visit(ASTExprLambda lambda) {
        var body = compileNode(lambda.body);
        if (body instanceof IRCompLambda bodyLambda) {
            return new IRCompLambda(lambda.param, getType(lambda.paramType), new IRCompProduce(new IRValueThunk(bodyLambda, Map.of())));
        }
        else if (body instanceof IRComp bodyComp) {
            return new IRCompLambda(lambda.param, getType(lambda.paramType), bodyComp);
        }
        else if (body instanceof IRValue bodyValue) {
            return new IRCompLambda(lambda.param, getType(lambda.paramType), new IRCompProduce(bodyValue));
        }
        return error("Lambda expected body of computation or value, received: " + lambda.body);
    }

    @Override
    public IRNode visit(ASTExprLetIn let) {
        var expr = compileNode(let.expr);
        var body = compileNode(let.body);

        if (expr instanceof IRCompLambda exprLambda && body instanceof IRComp bodyComp) {
            return new IRCompLet(let.name.name, new IRValueThunk(exprLambda, Map.of()), bodyComp);
        }
        else if (expr instanceof IRComp exprComp && body instanceof IRComp bodyComp) {
            return new IRCompDo(let.name.name, exprComp, bodyComp);
        }
        else if (expr instanceof IRValue exprValue && body instanceof IRComp bodyComp) {
            return new IRCompLet(let.name.name, exprValue, bodyComp);
        }
        else if (expr instanceof IRValue exprValue && body instanceof IRValue bodyValue) {
            return new IRCompLet(let.name.name, exprValue, new IRCompProduce(bodyValue));
        }
        else if (expr instanceof IRComp exprComp && body instanceof IRValue bodyValue) {
            return new IRCompDo(let.name.name, exprComp, new IRCompProduce(bodyValue));
        }

        return error("let-in:\nexpr: " + expr + "\nbody: " + body);
    }

    @Override
    public IRNode visit(ASTExprCaseOfBranch ofBranch) {
        var pattern = compileNode(ofBranch.pattern);
        var body = compileNode(ofBranch.body);
        if (pattern instanceof IRValue patternValue && body instanceof IRComp bodyComp) {
            return new IRCompCaseOfBranch(patternValue, bodyComp);
        }
        else if (pattern instanceof IRValue patternValue && body instanceof IRValue bodyValue) {
            return new IRCompCaseOfBranch(patternValue, new IRCompProduce(bodyValue));
        }
        return error("Case-of-branch expects Value for pattern and Computation for body:\npattern: " + pattern + "\nbody: " + body);
    }

    private boolean patternMatches(IRValue value, IRValue pattern) {
        if (pattern instanceof IRValueName) {
            return true;
        }
        else if (value instanceof IRValueNumber valueNumber && pattern instanceof IRValueNumber patternNumber) {
            return valueNumber.value == patternNumber.value;
        }
        else if (value instanceof IRValueString valueString && pattern instanceof IRValueString patternString) {
            return valueString.value.equals(patternString.value);
        }
        else if (value instanceof IRValueUnit && pattern instanceof IRValueUnit) {
            return true;
        }
        else if (value instanceof IRValueTuple valueTuple && pattern instanceof IRValueTuple patternTuple) {
            if (valueTuple.values.size() != patternTuple.values.size()) {
                return false;
            }
            for (int i = 0; i < valueTuple.values.size(); i++) {
                var valueElem = valueTuple.values.get(i);
                var patternElem = patternTuple.values.get(i);
                if (!patternMatches(valueElem, patternElem)) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public IRNode visit(ASTExprCaseOf caseOf) {
        var compiledValue = compileNode(caseOf.value);
        var value = compiledValue;
        if (value instanceof IRComp) {
            var argname = "_pmarg_" + (args++);
            value = new IRValueName(argname);
        }
//        if (!(value instanceof IRValue caseOfValue)) {
//            return error("Case-of expects value, received: " + value);
//        }

        var maybeBranches = caseOf.cases.stream().map(this::compileNode).toList();
        for (IRNode branch : maybeBranches) {
            if (!(branch instanceof IRCompCaseOfBranch)) {
                return error("Expected IRBranchCaseOf, received: " + branch);
            }
        }
        var branches = maybeBranches.stream().map(b -> (IRCompCaseOfBranch) b).toList();

        for (IRCompCaseOfBranch branch : branches) {
            // Type-check branch patterns and bodies here.
        }

        if (compiledValue instanceof IRComp caseOfComp) {
            return new IRCompDo(((IRValueName) value).name, caseOfComp, new IRCompCaseOf((IRValueName) value, branches));
        }
        else if (value instanceof IRValue caseOfValue) {
            return new IRCompCaseOf(caseOfValue, branches);
        }
        else {
            return error("Unhandled case of value: " + value);
        }
    }

    @Override
    public IRNode visit(ASTExprTuple tuple) {
        var values = tuple.values.stream().map(this::compileNode).toList();
        if (values.stream().allMatch(t -> t instanceof IRValue)) {
            var tupleValues = values.stream().map(t -> (IRValue) t).toList();
//            var tupleTypes = tupleValues.stream().map(t -> type)
//            return new IRValueAnnotated(new IRValueTuple(tupleValues), new IRTypeTuple(tupleTypes));
            return new IRValueTuple(tupleValues);
        }
        else {
            // Should tuple construction be lazy or eager?
            // Eager for now, return a value of tuple not a thunk of producer of tuple.
            // Need to bind via do-in each computation in the tuple, values are fine as is.
            var comps = new ArrayList<IRComp>();
            var names = new ArrayList<String>();
            for (IRNode node : values) {
                if (node instanceof IRComp comp) {
                    comps.add(comp);
                    names.add("__arg_" + (args++));
                }
            }

            var innerValues = new ArrayList<IRValue>();
            int compCount = 0;
            for (int i = 0; i < values.size(); i++) {
                var v = values.get(i);
                if (v instanceof IRComp) {
                    innerValues.add(new IRValueName(names.get(compCount)));
                    compCount += 1;
                }
                else if (v instanceof IRValue val) {
                    innerValues.add(val);
                }
                else {
                    return error("Bad value for tuple: " + v);
                }
            }

            var innerTuple = new IRValueTuple(innerValues);
            IRComp wrappedTuple = new IRCompProduce(innerTuple);
            for (int i = 0; i < compCount; i++) {
                wrappedTuple = new IRCompDo(names.get(i), comps.get(i), wrappedTuple);
            }
            return wrappedTuple;
        }
    }

    @Override
    public IRNode visit(ASTExprName name) {
        return new IRValueName(name.name);
    }

    @Override
    public IRNode visit(ASTExprSymbolic symbolic) {
        return new IRValueName(symbolic.symbol);
    }

    @Override
    public IRNode visit(ASTExprNumber number) {
        return new IRValueAnnotated(new IRValueNumber(number.value), new IRTypeName("Number"));
    }

    @Override
    public IRNode visit(ASTExprString string) {
        return new IRValueAnnotated(new IRValueString(string.value), new IRTypeName("String"));
    }

    @Override
    public IRNode visit(ASTExprParen paren) {
        return compileNode(paren.expr);
    }

    public IRNode error(String msg) {
        throw new RuntimeException("[Compile Error] " + msg);
    }
}
