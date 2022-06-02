package dev.foltz.mooselang.typing;

import dev.foltz.mooselang.ast.ASTDefaultVisitor;
import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.statement.ASTStmtTypeDef;
import dev.foltz.mooselang.ast.typing.*;
import dev.foltz.mooselang.typing.types.*;
import dev.foltz.mooselang.typing.types.valuetypes.TypeValueBool;
import dev.foltz.mooselang.typing.types.valuetypes.TypeValueInt;
import dev.foltz.mooselang.typing.types.valuetypes.TypeValueString;

import java.util.*;
import java.util.stream.Collectors;

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

    public void recursiveType(String name) {
        this.namedTypes.put(name, new TypeName(name));
    }

    public Type evalType(ASTExpr node) {
        return node.accept(this);
    }

    public Type evalType(ASTType node) {
        return node.accept(this);
    }

    public Type unifyTypes(Type a, Type b) {
        if (equalTypes(a, b)) {
            return a;
        }
        else if (isSubtype(a, b)) {
            return b;
        }
        else if (isSubtype(b, a)) {
            return a;
        }

        if (a == NoType.INSTANCE || b == NoType.INSTANCE) {
            return NoType.INSTANCE;
        }

        return new TypeUnion(List.of(a, b));
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

        if (sub instanceof TypeUnion tu) {
            if (tu.types.stream().allMatch(t -> isSubtype(t, sup))) {
                return true;
            }
        }

        if (sub instanceof TypeRecord tuSub && sup instanceof TypeRecord tuSup) {
            Map<String, Type> remaining = new HashMap<>(tuSup.fields);
            for (Map.Entry<String, Type> entry : tuSub.fields.entrySet()) {
                var name = entry.getKey();
                var type = entry.getValue();
                if (remaining.containsKey(name)) {
                    if (isSubtype(type, remaining.get(name))) {
                        remaining.remove(name);
                    }
                    else {
                        System.out.println("isSubtype failed on record, field names have differing types: " + name + " :: " + type + " vs. " + remaining.get(name));
                        return false;
                    }
                }
            }

            return remaining.isEmpty();
        }

        if (sub instanceof TypeValueInt && sup instanceof TypeInt) {
            return true;
        }
        else if (sub instanceof TypeValueBool && sup instanceof TypeBool) {
            return true;
        }
        else if (sub instanceof TypeValueString && sup instanceof TypeString) {
            return true;
        }

        return equalTypes(sub, sup);
    }

    public Type accessTypeField(Type t, String fieldName) {
        if (t instanceof TypeRecord rec) {
            return rec.fields.getOrDefault(fieldName, NoType.INSTANCE);
        }

        if (t instanceof TypeUnion union) {
            for (Type u : union.types) {
                Type f = accessTypeField(u, fieldName);
                if (f != NoType.INSTANCE) {
                    return f;
                }
            }
        }

        return NoType.INSTANCE;
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

        if (a instanceof TypeValueInt ta && b instanceof TypeValueInt tb) {
            return ta.value() == tb.value();
        }
        else if (a instanceof TypeValueBool ta && b instanceof TypeValueBool tb) {
            return ta.value() == tb.value();
        }
        else if (a instanceof TypeValueString ta && b instanceof TypeValueString tb) {
            return ta.value().equals(tb.value());
        }

        return false;
    }

    @Override
    public Type visit(ASTTypeRecord node) {
        var fields = node.fields.entrySet().stream()
            .map(f -> new AbstractMap.SimpleEntry<>(f.getKey().name(), evalType(f.getValue())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new TypeRecord(fields);
    }

    @Override
    public Type visit(ASTExprFieldAccess node) {
        List<String> accessPath = new ArrayList<>();
        ASTExprFieldAccess innermostAccess = node;

        while (innermostAccess.lhs instanceof ASTExprFieldAccess innerAccess) {
            accessPath.add(0, innermostAccess.fieldName.name());
            innermostAccess = innerAccess;
        }
        accessPath.add(0, innermostAccess.fieldName.name());
        ASTExpr head = innermostAccess.lhs;

        var typeAccess = evalType(head);
        for (int i = 0; i < accessPath.size(); i++) {
            var fieldName = accessPath.get(i);
            typeAccess = accessTypeField(typeAccess, fieldName);
            if (typeAccess == NoType.INSTANCE) {
                return NoType.INSTANCE;
            }
        }
        return typeAccess;
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

        return unifyTypes(typeTrue, typeFalse);
    }

    @Override
    public Type visit(ASTTypeValue node) {
        var expr = node.value();
        if (expr instanceof ASTExprInt exprInt) {
            return new TypeValueInt(exprInt.value());
        }
        else if (expr instanceof ASTExprBool exprBool) {
            return new TypeValueBool(exprBool.value());
        }
        else if (expr instanceof ASTExprString exprString) {
            return new TypeValueString(exprString.value());
        }

        throw new IllegalStateException("Expected type literal during type check, found: " + node);
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
        return new TypeValueBool(node.value());
    }

    @Override
    public Type visit(ASTExprNone node) {
        return TypeNone.INSTANCE;
    }

    @Override
    public Type visit(ASTExprString node) {
        return new TypeValueString(node.value());
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
    public Type visit(ASTExprRecord node) {
        var typedFields = node.fields.entrySet().stream()
            .map(e -> {
                var name = e.getKey();
                var value = e.getValue();

                var typeValue = evalType(value);
                return new AbstractMap.SimpleEntry<>(name.name(), typeValue);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (typedFields.values().stream().anyMatch(t -> t == NoType.INSTANCE)) {
            return NoType.INSTANCE;
        }

        return new TypeRecord(typedFields);
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
        return new TypeValueInt(node.value());
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
