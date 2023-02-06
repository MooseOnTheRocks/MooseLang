package dev.foltz.mooselang.typing;

import dev.foltz.mooselang.ast.*;
import dev.foltz.mooselang.typing.comp.CompType;
import dev.foltz.mooselang.typing.comp.Lambda;
import dev.foltz.mooselang.typing.comp.Producer;
import dev.foltz.mooselang.typing.comp.StackPush;
import dev.foltz.mooselang.typing.value.*;

public class TypedAST extends ASTVisitor<TypedAST> {
    public final Scope context;
    public final IRType result;

    public TypedAST(Scope context, IRType lastType) {
        this.context = context;
        this.result = lastType;
    }

    private TypedAST error(String msg) {
        throw new IllegalStateException("[Typing Error] " + msg);
    }

    private TypedAST typed(IRType type) {
        return new TypedAST(context, type);
    }

    public TypedAST pushContext(String key, ValueType value) {
        return new TypedAST(context.push().put(key, value), result);
    }

    public TypedAST popContext() {
        return new TypedAST(context.pop(), result);
    }

    public TypedAST evalTypeAST(ASTNode node) {
        return node.apply(this);
    }

    @Override
    public TypedAST visit(ExprApply apply) {
        var lhs = apply.lhs;
        var rhs = apply.rhs;
        if (lhs instanceof ExprName ename) {
            return error("Cannot apply lhs " + lhs + " to rhs " + rhs);
        }
        else {
            var lhsType = evalTypeAST(lhs).result;
            if (lhsType instanceof Thunk thunk) {
                var compType = thunk.comp;
                if (compType instanceof Lambda lambda) {
                    return typed(lambda.bodyType);
                }
            }
            return error("Unable to apply " + lhs + " to " + rhs);
        }
    }

    @Override
    public TypedAST visit(ExprChain chain) {
        var firstType = evalTypeAST(chain.first);
        var secondType = evalTypeAST(chain.second);
        if (firstType.result instanceof StackPush push && secondType.result instanceof Lambda lambda) {
            return typed(lambda.bodyType);
        }
        else if (firstType.result instanceof CompType firstComp && secondType.result instanceof CompType secondComp) {
            return typed(secondComp);
        }
        else {
            return error("Cannot chain:" + "\n"
                    + "-- AST:\n" + chain.first + " with " + chain.second + "\n"
                    + "-- Types:\n" + firstType.result + " with " + secondType.result);
        }
    }

    @Override
    public TypedAST visit(ExprDirective directive) {
        switch (directive.name.name) {
            case "produce" -> {
                var rhsType = evalTypeAST(directive.body).result;
                if (rhsType instanceof ValueType value) {
                    return typed(new Producer(value));
                } else {
                    return error("produce expects rhs of value type, received: " + rhsType);
                }
            }
            case "thunk" -> {
                var rhsType = evalTypeAST(directive.body).result;
                if (rhsType instanceof CompType comp) {
                    return typed(new Thunk(comp));
                } else {
                    return error("thunk expects rhs of computation type, received: " + rhsType);
                }
            }
            case "force" -> {
                var rhsType = evalTypeAST(directive.body).result;
                if (rhsType instanceof Thunk thunk) {
                    return typed(thunk.comp);
                } else {
                    return error("force expects rhs of thunk type, received: " + rhsType);
                }
            }
            case "push" -> {
                var rhsType = evalTypeAST(directive.body).result;
                if (rhsType instanceof ValueType value) {
                    return typed(new StackPush(value));
                }
                else {
                    return error("Push expects rhs of value type, received: " + rhsType);
                }
            }
            default -> {
                return error("Unknown directive: " + directive.name.name);
            }
        }
    }

    @Override
    public TypedAST visit(ExprLambda lambda) {
        var paramType = switch (lambda.paramType) {
            case "Number" -> new NumberType();
            case "()" -> new Unit();
            default -> throw new RuntimeException("Invalid lambda parameter type: " + lambda.paramType);
        };
        var bodyType = pushContext(lambda.param, paramType).evalTypeAST(lambda.body);
        if (bodyType.result instanceof CompType comp) {
            return bodyType.popContext().typed(new Lambda(lambda.param, paramType, comp));
        }
        else {
            return error("Lambda expects body of computation type, received: " + bodyType.result);
        }
    }

    @Override
    public TypedAST visit(ExprLetCompIn letIn) {
        var name = letIn.name;
        var exprType = evalTypeAST(letIn.expr).result;
        if (exprType instanceof Producer prod) {
            var s1 = pushContext(name.name, prod.value);
            var s2 = s1.evalTypeAST(letIn.body);
            if (s2.result instanceof CompType comp) {
                return s2.popContext();
            }
            else {
                return error("Let-in expected computation for body, received: " + s2.result);
            }
        }
        else {
            return error("Let-in expected computation for expr, received: " + exprType);
        }
    }

    @Override
    public TypedAST visit(ExprLetValueIn letIn) {
        var name = letIn.name;
        var exprType = evalTypeAST(letIn.expr).result;
        if (exprType instanceof ValueType value) {
            var s1 = pushContext(name.name, value);
            var s2 = s1.evalTypeAST(letIn.body);
            if (s2.result instanceof CompType comp) {
                return s2.popContext();
            }
            else {
                return error("Let-in expected computation for body, received: " + s2.result);
            }
        }
        else {
            return error("Let-in expected value for expr, recieved: " + exprType);
        }
    }

    @Override
    public TypedAST visit(ExprName name) {
        return context.find(name.name)
            .map(this::typed)
            .orElseGet(() -> {
                System.err.println(context.find(name.name));
                throw new RuntimeException("Cannot find: " + name);
            });
    }

    @Override
    public TypedAST visit(ExprNumber number) {
        return typed(new NumberType());
    }

    @Override
    public TypedAST visit(ExprString string) {
        return typed(new StringType());
    }

    @Override
    public TypedAST visit(ExprParen paren) {
        return evalTypeAST(paren.expr);
    }

    @Override
    public TypedAST visit(ExprSymbolic symbolic) {
        return context.find(symbolic.symbol)
            .map(this::typed)
            .orElseGet(() -> {
                System.err.println(context.find(symbolic.symbol));
                throw new RuntimeException("Cannot find: " + symbolic.symbol);
            });
    }
}
