package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ast.VisitorAST;
import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.value.NumberType;
import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.Unit;
import dev.foltz.mooselang.typing.value.ValueType;

public class CompilerIR extends VisitorAST<IRNode> {
    // TODO: Move this somewhere else.
    public ValueType getType(String typeName) {
        return switch (typeName) {
            case "Number" -> new NumberType();
            case "String" -> new StringType();
            case "Unit" -> new Unit();
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

//    @Override
//    public IRNode visit(ExprChain chain) {
//        if (chain.first instanceof ExprDirective directive && directive.name.name.equals("push")) {
//            var pushBody = compile(directive.body);
//            if (!(pushBody instanceof IRValue pushValue)) {
//                return error("#push expects rhs of value, received: " + directive.body + " >>> " + pushBody);
//            }
//            var csecond = compile(chain.second);
//            if (!(csecond instanceof IRComp secondComp)) {
//                return error("';' expects rhs of computation, received: " + chain.second + " >>> " + csecond);
//            }
//            return new IRPush(pushValue, secondComp);
//        }
//
//        var cfirst = compile(chain.first);
//        if (!(cfirst instanceof IRComp firstComp)) {
//            return error("';' expects lhs of computation, received: " + chain.first + " >>> " + cfirst);
//        }
//
//        var csecond = compile(chain.second);
//        if (!(csecond instanceof IRComp secondComp)) {
//            return error("';' expects rhs of computation, received: " + chain.second + " >>> " + csecond);
//        }
//
//        return new IRLetComp("_", firstComp, secondComp);
//    }
//
//    @Override
//    public IRNode visit(ExprDirective directive) {
//        switch (directive.name.name) {
//            case "produce": {
//                var cbody = compile(directive.body);
//                if (!(cbody instanceof IRValue bodyValue)) {
//                    return error("#produce expects value, received: " + cbody);
//                }
//                return new IRProduce(bodyValue);
//            }
//            case "push": {
//                return error("Push should have been handled in a chain expression!");
//            }
//            case "thunk": {
//                var cbody = compile(directive.body);
//                if (cbody instanceof IRComp bodyComp) {
//                    return new IRThunk(bodyComp);
//                }
//                else {
//                    return error("#thunk expects computation, received: " + cbody);
//                }
//            }
//            case "force": {
//                var cbody = compile(directive.body);
//                if (cbody instanceof IRThunk bodyThunk) {
//                    return new IRForceThunk(bodyThunk);
//                }
//                else if (cbody instanceof IRName bodyName) {
//                    return new IRForceName(bodyName.name);
//                }
//                else {
//                    return error("#force expects thunk, received: " + cbody);
//                }
//            }
//            default: {
//                return error("Unknown directive: " + directive);
//            }
//        }
//    }

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

        return error("let-in:\nexpr: " + expr + "\nbody: " + body);
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
