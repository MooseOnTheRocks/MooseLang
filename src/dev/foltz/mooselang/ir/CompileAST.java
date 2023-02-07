package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ast.*;
import dev.foltz.mooselang.rt.Interpreter;

public class CompileAST extends ASTVisitor<IRNode> {
    public IRNode compile(ASTNode node) {
        return node.apply(this);
    }

    @Override
    public IRNode visit(ExprChain chain) {
        if (chain.first instanceof ExprDirective directive && directive.name.name.equals("push")) {
            var pushBody = compile(directive.body);
            if (!(pushBody instanceof IRValue pushValue)) {
                return error("#push expects rhs of value, received: " + directive.body + " >>> " + pushBody);
            }
            var csecond = compile(chain.second);
            if (!(csecond instanceof IRComp secondComp)) {
                return error("';' expects rhs of computation, received: " + chain.second + " >>> " + csecond);
            }
            return new IRPush(pushValue, secondComp);
        }

        var cfirst = compile(chain.first);
        if (!(cfirst instanceof IRComp firstComp)) {
            return error("';' expects lhs of computation, received: " + chain.first + " >>> " + cfirst);
        }

        var csecond = compile(chain.second);
        if (!(csecond instanceof IRComp secondComp)) {
            return error("';' expects rhs of computation, received: " + chain.second + " >>> " + csecond);
        }

        return new IRLetComp("_", firstComp, secondComp);
    }

    @Override
    public IRNode visit(ExprDirective directive) {
        switch (directive.name.name) {
            case "produce": {
                var cbody = compile(directive.body);
                if (!(cbody instanceof IRValue bodyValue)) {
                    return error("#produce expects value, received: " + cbody);
                }
                return new IRProduce(bodyValue);
            }
            case "push": {
                return error("Push should have been handled in a chain expression!");
                /*
                var cbody = compile(directive.body);
                if (cbody instanceof IRValue bodyValue) {
                    return new IRPush(bodyValue);
                }
                else {
                    return error("#push expects value, received: " + cbody);
                }
                */
            }
            case "thunk": {
                var cbody = compile(directive.body);
                if (cbody instanceof IRComp bodyComp) {
                    return new IRThunk(bodyComp);
                }
                else {
                    return error("#thunk expects computation, received: " + cbody);
                }
            }
            case "force": {
                var cbody = compile(directive.body);
                if (cbody instanceof IRThunk bodyThunk) {
                    return new IRForceThunk(bodyThunk);
                }
                else if (cbody instanceof IRName bodyName) {
                    return new IRForceName(bodyName.name);
                }
                else {
                    return error("#force expects thunk, received: " + cbody);
                }
            }
            default: {
                return error("Unknown directive: " + directive);
            }
        }
    }

    @Override
    public IRNode visit(ExprLambda lambda) {
        var cbody = compile(lambda.body);
        if (cbody instanceof IRComp bodyComp) {
            return new IRLambda(lambda.param, bodyComp);
        }
        else {
            return error("Lambda expects body of computation, received: " + lambda.body + " >>> " + cbody);
        }
    }

    @Override
    public IRNode visit(ExprLetCompIn letIn) {
        var expr = letIn.expr;
        var cexpr = compile(expr);
        if (!(cexpr instanceof IRComp exprComp)) {
            return error("LetComp expects computation for expression, received: " + expr + " >>> " + cexpr);
        }

        var body = letIn.body;
        var cbody = compile(body);
        if (!(cbody instanceof IRComp bodyComp)) {
            return error("LetComp expects computation for body, received: " + body + " >>> " + cbody);
        }

        return new IRLetComp(letIn.name.name, exprComp, bodyComp);
    }

    @Override
    public IRNode visit(ExprLetValueIn letIn) {
        var expr = letIn.expr;
        var cexpr = compile(expr);
        if (!(cexpr instanceof IRValue exprValue)) {
            return error("LetValue expects value for expression, received: " + expr + " >>> " + cexpr);
        }

        var body = letIn.body;
        var cbody = compile(body);
        if (!(cbody instanceof IRComp bodyComp)) {
            return error("LetValue expects computation for body, received: " + body + " >>> " + cbody);
        }

        return new IRLetValue(letIn.name.name, exprValue, bodyComp);
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
        return compile(paren.expr);
    }

    public IRNode error(String msg) {
        throw new RuntimeException("[Compile Error] " + msg);
    }
}
