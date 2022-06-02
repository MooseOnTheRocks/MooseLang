package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.statement.ASTStmtTypeDef;
import dev.foltz.mooselang.ast.typing.ASTTypeValue;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeRecord;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;

import java.util.Arrays;
import java.util.List;

public class ASTPrinter extends ASTDefaultVisitor<StringBuilder> {
    private final StringBuilder sb;

    public ASTPrinter() {
        super((astNode) -> { throw new UnsupportedOperationException("Printer cannot visit " + astNode); });
        sb = new StringBuilder();
    }

    protected void emit() {
        sb.append("\n");
    }

    protected void emit(Object ...objs) {
        Arrays.stream(objs).forEach(obj -> {
            if (obj instanceof ASTNode node) {
                node.accept(this);
            }
            else {
                sb.append(obj);
            }
        });
    }

    protected void emitJoin(String sep, List<?> objs) {
        for (int i = 0; i < objs.size(); i++) {
            Object obj = objs.get(i);
            emit(obj);
            if (i != objs.size() - 1) {
                emit(", ");
            }
        }
    }

    public static String print(ASTNode node) {
        ASTPrinter printer = new ASTPrinter();
        return node.accept(printer).toString();
    }

    @Override
    public StringBuilder visit(ASTTypeRecord node) {
        emit("TypeRecord(");
        emitJoin(", ", node.fields.entrySet().stream().map(f -> f.getKey() + ": " + f.getValue()).toList());
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtTypeDef node) {
        emit("StmtTypeDef(", node.name, ", ", node.type, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprCall node) {
        emit("ExprCall(", node.name, ", [");
        emitJoin(", ", node.params);
        emit("])");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprTyped<? extends ASTExpr> node) {
        emit(node.expr, ": ", node.type);
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtLet node) {
        emit("StmtLet(", node.getName(), ", ", node.body, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtFuncDef node) {
        emit("StmtFuncDef(", node.name, ", (");
        emitJoin(", ", node.typedParams);
        emit("), ", node.retType, ", ", node.body, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprNone node) {
        emit("ExprNone()");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprBool node) {
        emit("ExprBool(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprInt node) {
        emit("ExprInt(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprName node) {
        emit("ExprName(", node.name(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprString node) {
        emit("ExprString(\"", node.value(), "\")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprIfThenElse node) {
        emit("ExprIfThenElse(", node.predicate(), ", ", node.exprTrue(), ", ", node.exprFalse(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeName node) {
        emit("TypeName(", node.name(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeUnion node) {
        emit("TypeUnion(");
        emitJoin(", ", node.types());
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTTypeValue node) {
        emit("TypeLiteral(", node.value(), ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprRecord node) {
        emit("ExprRecord(");
        emitJoin(", ", node.fields.entrySet().stream().map(e -> e.getKey() + " = " + e.getValue()).toList());
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprFieldAccess node) {
        emit("ExprFieldAccess(", node.lhs, ", ", node.fieldName, ")");
        return sb;
    }
}
