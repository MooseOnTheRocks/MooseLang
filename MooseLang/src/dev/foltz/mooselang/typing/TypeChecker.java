package dev.foltz.mooselang.typing;

import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.ASTExprTyped;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.statement.ASTStmtTypeDef;
import dev.foltz.mooselang.interpreter.Env;
import dev.foltz.mooselang.typing.types.NoType;
import dev.foltz.mooselang.typing.types.Type;
import dev.foltz.mooselang.typing.types.TypeFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypeChecker {
    public final Env<Type> namedTypes;
    public final Env<Type> globalTypedNames;

    private TypeChecker(Env<Type> namedTypes, Env<Type> globals) {
        this.namedTypes = new Env<>(namedTypes);
        this.globalTypedNames = new Env<>(globals);
    }

    public static TypeChecker withTypesAndGlobals(Map<String, Type> namedTypes, Map<String, Type> globals) {
        return new TypeChecker(namedTypes, globals);
    }

    public void bindNamedType(String name, Type type) {
        if (namedTypes.containsKey(name)) {
            throw new IllegalArgumentException("Attempt to rebind named type: " + name);
        }
        namedTypes.put(name, type);
    }

    public void bindNamedValueType(String name, Type type) {
        if (globalTypedNames.containsKey(name)) {
            throw new IllegalArgumentException("Attempt to retype named value: " + name);
        }
        globalTypedNames.put(name, type);
    }

    public Type typeCheck(ASTStmtLet stmt) {
        var localChecker = new ASTLocalTypeChecker(namedTypes, globalTypedNames);
        Type t = stmt.accept(localChecker);
//        if (t != NoType.INSTANCE) {
//            bindGlobalName(name, t);
//        }
        return t;
    }

    public Type typeCheck(ASTStmtFuncDef funcDef) {
        var funcName = funcDef.name;
        var funcParams = funcDef.typedParams;
        var funcRetTypeAST = funcDef.retType;
        var funcBody = funcDef.body;

        if (globalTypedNames.containsKey(funcName.name())) {
            throw new IllegalStateException("Cannot redefine typed function: " + funcName.name());
        }

        var localChecker = new ASTLocalTypeChecker(namedTypes, globalTypedNames);

        var funcScope = new HashMap<>(globalTypedNames);
        var paramScope = new HashMap<String, Type>();
        var paramTypes = new ArrayList<Type>();
        for (ASTExprTyped<ASTExprName> typedParam : funcParams) {
            var paramName = typedParam.expr.name();
            var paramType = localChecker.evalType(typedParam.type);
            if (paramScope.containsKey(paramName)) {
                throw new IllegalStateException("Found duplicate parameter name in function definition: " + paramName);
            }
            paramTypes.add(paramType);
            paramScope.put(paramName, paramType);
        }
        funcScope.putAll(paramScope);

        localChecker = new ASTLocalTypeChecker(namedTypes, funcScope);

        var typeBody = localChecker.evalType(funcBody);
        var typeRet = funcRetTypeAST.isEmpty() ? typeBody : localChecker.evalType(funcRetTypeAST.get());

        if (localChecker.isSubtype(typeBody, typeRet)) {
            var typeFunc = new TypeFunc(paramTypes, typeRet);
//            bindGlobalName(funcName.name(), typeFunc);
            return typeFunc;
        }

        return NoType.INSTANCE;
    }

    public Type typeCheck(ASTStmtTypeDef typeDef) {
        var name = typeDef.name.name();
        if (namedTypes.containsKey(name)) {
            throw new IllegalStateException("Cannot redefine type: " + name);
        }
        var localChecker = new ASTLocalTypeChecker(namedTypes, globalTypedNames);
        localChecker.recursiveType(name);
        var type = localChecker.evalType(typeDef.type);
//        if (type != NoType.INSTANCE) {
//            bindType(name, type);
//        }
        return type;
    }
}
