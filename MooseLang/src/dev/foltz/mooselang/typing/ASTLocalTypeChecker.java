package dev.foltz.mooselang.typing;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.ASTExprBool;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprNone;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.statement.ASTStmtTypeDef;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;
import dev.foltz.mooselang.typing.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTLocalTypeChecker extends ASTDefaultVisitor<Type> {
    public final Map<String, Type> namedTypes;
    public final Map<String, Type> scopedTypedNames;

    public ASTLocalTypeChecker(Map<String, Type> namedTypes, Map<String, Type> scopedTypedNames) {
        super(node -> { throw new UnsupportedOperationException("Cannot locally type check: " + node); });
        this.namedTypes = new HashMap<>(namedTypes);
        this.scopedTypedNames = new HashMap<>(scopedTypedNames);
    }

    public ASTLocalTypeChecker() {
        this(Map.of(), Map.of());
    }

    public Type evalType(ASTExpr node) {
        return node.accept(this);
    }

    public Type evalType(ASTType node) {
        return node.accept(this);
    }

    public boolean isSubtype(Type sub, Type sup) {
        if (sup == AnyType.INSTANCE) {
            return true;
        }

        if (sub == NoType.INSTANCE) {
            return false;
        }

        if (sup instanceof TypeUnion tu) {
            if (tu.types.stream().anyMatch(t -> isSubtype(sub, t))) {
                return true;
            }
        }

        return equalTypes(sub, sup);
    }

    public boolean equalTypes(Type a, Type b) {
        if (a == NoType.INSTANCE || b == NoType.INSTANCE) {
            return false;
        }

        if (a.equals(b) || b.equals(a)) {
            return true;
        }

        if (a instanceof TypeUnion ta && b instanceof TypeUnion tb) {
            var tas = ta.types;
            var tab = tb.types;
            if (tas.size() != tab.size()) {
                return false;
            }

            List<Type> remaining = new ArrayList<>(ta.types);
            for (Type t : tab) {
                int index = remaining.indexOf(t);
                if (index == -1) {
                    return false;
                }
                remaining.remove(index);
            }

            return true;
        }

        return false;
    }

    @Override
    public Type visit(ASTExprCall node) {
        var opName = node.name;
        var params = node.params;

        var opType = evalType(opName);
        if (!(opType instanceof TypeFunc funcType)) {
            throw new IllegalStateException("Expected function type for name " + opName + ", received: " + opType);
        }

        if (funcType.paramTypes.size() != params.size()) {
            System.out.println("Incompatible arity!");
            return NoType.INSTANCE;
        }

        var paramTypes = params.stream().map(this::evalType).toList();

        System.out.println("ExprCall: funcType = " + funcType + ", paramTypes = " + paramTypes);

        for (int i = 0; i < params.size(); i++) {
            var funcParam = funcType.paramTypes.get(i);
            var exprParam = paramTypes.get(i);
            if (!isSubtype(exprParam, funcParam)) {
                System.out.println("Incompatible argument types!");
                return NoType.INSTANCE;
            }
        }

        return funcType.retType;
    }

    @Override
    public Type visit(ASTExprIfThenElse node) {
        var pred = node.predicate();
        var exprTrue = node.exprTrue();
        var exprFalse = node.exprFalse();

        var typePred = evalType(pred);
        var typeTrue = evalType(exprTrue);
        var typeFalse = evalType(exprFalse);

        if (!isSubtype(typePred, TypeBool.INSTANCE)) {
            return NoType.INSTANCE;
        }

        if (isSubtype(typeFalse, typeTrue)) {
            return typeTrue;
        }
        else if (isSubtype(typeTrue, typeFalse)) {
            return typeFalse;
        }

        return NoType.INSTANCE;
    }

    @Override
    public Type visit(ASTTypeName node) {
        var name = node.name();
        if (namedTypes.containsKey(name)) {
            return namedTypes.get(name);
        }
        return NoType.INSTANCE;
    }

    @Override
    public Type visit(ASTTypeUnion node) {
        var types = node.types().stream().map(this::evalType).toList();
        return new TypeUnion(types);
    }

    @Override
    public Type visit(ASTExprBool node) {
        return TypeBool.INSTANCE;
    }

    @Override
    public Type visit(ASTExprNone node) {
        return TypeNone.INSTANCE;
    }

    @Override
    public Type visit(ASTExprName node) {
        //TODO: Store map of solved type names with TypeChecker to determine previously derived types of names.
        var name = node.name();
        if (scopedTypedNames.containsKey(name)) {
            return scopedTypedNames.get(name);
        }
        return NoType.INSTANCE;
    }

    @Override
    public Type visit(ASTExprTyped<? extends ASTExpr> node) {
        var typeExpr = evalType(node.expr);
        var typeHint = evalType(node.type);

        if (node.expr instanceof ASTExprName && typeExpr == NoType.INSTANCE) {
            return typeHint;
        }

        if (isSubtype(typeExpr, typeHint)) {
            return typeHint;
        }

        return NoType.INSTANCE;
    }

    @Override
    public Type visit(ASTExprInt node) {
        return TypeInt.INSTANCE;
    }

    @Override
    public Type visit(ASTStmtLet node) {
        var name = node.getName();
        var body = node.body;

        var typeName = evalType(name);
        var typeBody = evalType(body);

        if (typeName == NoType.INSTANCE) {
            return typeBody;
        }
        else if (isSubtype(typeBody, typeName)) {
            return typeName;
        }
        else {
            return NoType.INSTANCE;
        }
    }

    @Override
    public Type visit(ASTStmtTypeDef node) {
        var name = node.name.name();
        if (namedTypes.containsKey(name)) {
            throw new IllegalStateException("Cannot redefine type: " + name);
        }
        var type = evalType(node.type);
        return type;
    }
}
