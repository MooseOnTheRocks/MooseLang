package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.value.*;

import java.util.ArrayList;
import java.util.function.Predicate;

public class CompilerIR extends VisitorAST<IRNode> {
    // TODO: Move this somewhere else.
    public TypeValue getType(String typeName) {
        return switch (typeName) {
            case "Number" -> new ValueNumber();
            case "String" -> new ValueString();
            case "Unit" -> new ValueUnit();
            default -> throw new RuntimeException("getType of unknown type: " + typeName);
        };
    }

    public static IRNode compile(ASTNode node) {
        return new CompilerIR().compileNode(node);
    }

    public IRNode compileNode(ASTNode node) {
        return node.apply(this);
    }

    static int args = 0;
    @Override
    public IRNode visit(ExprApply apply) {
        var lhs = compileNode(apply.lhs);
        var rhs = compileNode(apply.rhs);

        // Assume this is function application so name corresponds to (A -> B)
        if (lhs instanceof IRName name) {
            // Bind evaluation of rhs to scope, then apply to lhs as value with push.
            if (rhs instanceof IRComp rhsComp) {
                var argname = "__arg_" + (args++);
                return new IRDo(argname, rhsComp, new IRPush(new IRName(argname), new IRForce(name)));
            }
            else if (rhs instanceof IRValue rhsValue) {
                return new IRPush(rhsValue, new IRForce(name));
            }
            else {
                return error("");
            }
        }
        else if (lhs instanceof IRLambda lhsLambda && rhs instanceof IRComp rhsComp) {
            var argname = "__app_" + (args++);
            return new IRDo(argname, rhsComp, new IRPush(new IRName(argname), lhsLambda));
        }
        else if (lhs instanceof IRComp lhsComp && rhs instanceof IRValue rhsValue) {
            var argname = "__app_" + (args++);
            return new IRDo(argname, lhsComp, new IRPush(rhsValue, new IRForce(new IRName(argname))));
        }
        else if (lhs instanceof IRValue lhsValue && rhs instanceof IRLambda rhsLambda) {
            return new IRPush(lhsValue, rhsLambda);
        }
        else {
            return error("Application failed:\nlhs: " + lhs + "\nrhs: " + rhs);
        }
    }

    @Override
    public IRNode visit(ExprLambda lambda) {
        var body = compileNode(lambda.body);
        if (body instanceof IRLambda bodyLambda) {
            return new IRLambda(lambda.param, getType(lambda.paramType), new IRProduce(new IRThunk(bodyLambda)));
        }
        else if (body instanceof IRComp bodyComp) {
            return new IRLambda(lambda.param, getType(lambda.paramType), bodyComp);
        }
        else if (body instanceof IRValue bodyValue) {
            return new IRLambda(lambda.param, getType(lambda.paramType), new IRProduce(new IRName(lambda.param)));
        }
        return error("Lambda expected body of computation, received: " + lambda.body);
    }

    @Override
    public IRNode visit(ExprLetIn let) {
        var expr = compileNode(let.expr);
        var body = compileNode(let.body);

        if (expr instanceof IRLambda exprLambda && body instanceof IRComp bodyComp) {
            return new IRLet(let.name.name, new IRThunk(exprLambda), bodyComp);
        }
        else if (expr instanceof IRComp exprComp && body instanceof IRComp bodyComp) {
            return new IRDo(let.name.name, exprComp, bodyComp);
        }
        else if (expr instanceof IRValue exprValue && body instanceof IRComp bodyComp) {
            return new IRLet(let.name.name, exprValue, bodyComp);
        }
        else if (expr instanceof IRValue exprValue && body instanceof IRValue bodyValue) {
            return new IRLet(let.name.name, exprValue, new IRProduce(bodyValue));
        }

        return error("let-in:\nexpr: " + expr + "\nbody: " + body);
    }

    @Override
    public IRNode visit(ExprCaseOfBranch ofBranch) {
        var pattern = compileNode(ofBranch.pattern);
        var body = compileNode(ofBranch.body);
        if (pattern instanceof IRValue patternValue && body instanceof IRComp bodyComp) {
            return new IRCaseOfBranch(patternValue, bodyComp);
        }
        else if (pattern instanceof IRValue patternValue && body instanceof IRValue bodyValue) {
            return new IRCaseOfBranch(patternValue, new IRProduce(bodyValue));
        }
        return error("Case-of-branch expects Value for pattern and Computation for body:\npattern: " + pattern + "\nbody: " + body);
    }

    @Override
    public IRNode visit(ExprCaseOf caseOf) {
        var value = compileNode(caseOf.value);
        if (!(value instanceof IRValue caseOfValue)) {
            return error("Case-of expects value, received: " + value);
        }

        var maybeBranches = caseOf.cases.stream().map(this::compileNode).toList();
        for (IRNode branch : maybeBranches) {
            if (!(branch instanceof IRCaseOfBranch)) {
                return error("Expected IRBranchCaseOf, received: " + branch);
            }
        }
        var branches = maybeBranches.stream().map(b -> (IRCaseOfBranch) b).toList();

        Predicate<IRCaseOfBranch> branchPred = b -> true;
        if (value instanceof IRTuple valueTuple) {
            branchPred = b ->
                b.pattern instanceof IRTuple pattern &&
                pattern.values.size() == valueTuple.values.size() &&
                pattern.values.stream().allMatch(v -> v instanceof IRName);
        }

        for (IRCaseOfBranch branch : branches) {
            if (branchPred.negate().test(branch)) {
                return error("Invalid CaseOfBranch: " + branch);
            }
        }

        return new IRCaseOf(caseOfValue, branches);
    }

    @Override
    public IRNode visit(ExprTuple tuple) {
        var values = tuple.values.stream().map(this::compileNode).toList();
        if (values.stream().allMatch(t -> t instanceof IRValue)) {
            return new IRTuple(values.stream().map(t -> (IRValue) t).toList());
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
                    innerValues.add(new IRName(names.get(compCount)));
                    compCount += 1;
                }
                else if (v instanceof IRValue val) {
                    innerValues.add(val);
                }
                else {
                    return error("Bad value for tuple: " + v);
                }
            }

            var innerTuple = new IRTuple(innerValues);
            IRComp wrappedTuple = new IRProduce(innerTuple);
            for (int i = 0; i < compCount; i++) {
                wrappedTuple = new IRDo(names.get(i), comps.get(i), wrappedTuple);
            }
            return wrappedTuple;
        }
    }

    @Override
    public IRNode visit(ExprName name) {
        return new IRName(name.name);
    }

    @Override
    public IRNode visit(ExprSymbolic symbolic) {
        return new IRName(symbolic.symbol);
    }

    @Override
    public IRNode visit(ExprNumber number) {
        return new IRNumber(number.value);
    }

    @Override
    public IRNode visit(ExprString string) {
        return new IRString(string.value);
    }

    @Override
    public IRNode visit(ExprParen paren) {
        return compileNode(paren.expr);
    }

    public IRNode error(String msg) {
        throw new RuntimeException("[Compile Error] " + msg);
    }
}
