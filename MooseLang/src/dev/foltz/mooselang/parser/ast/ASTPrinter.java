package dev.foltz.mooselang.parser.ast;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprList;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprNone;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;
import dev.foltz.mooselang.parser.ast.statements.*;

public class ASTPrinter implements ASTVisitor<StringBuilder> {
    private final StringBuilder sb;
    private final String indent;
    private int indentLevel;
    private boolean newline = false;

    public ASTPrinter() {
        this.sb = new StringBuilder();
        indent = "    ";
        indentLevel = 0;
    }

    public String toString() {
        return sb.toString();
    }

    private void indent() {
        indentLevel += 1;
    }

    private void dedent() {
        indentLevel -= 1;
    }

    private String indentStr() {
        return indent.repeat(Math.max(0, indentLevel));
    }

    public void emit(Object ...objs) {
        for (Object obj : objs) {
            if (newline) {
                sb.append(indentStr());
                newline = false;
            }
            sb.append(obj);
        }
    }

    public void emit() {
        sb.append("\n");
        newline = true;
    }

    @Override
    public StringBuilder visit(ASTExprInt node) {
        emit("Int(", node.value, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprString node) {
        emit("String(\"", node.value, "\")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprList node) {
        if (node.elements.isEmpty()) {
            emit("List()");
            return sb;
        }

        emit("List(");
        emit();
        indent();
        for (int i = 0; i < node.elements.size(); i++) {
            ASTExpr elem = node.elements.get(i);
            elem.accept(this);
            if (i != node.elements.size() - 1) {
                emit(", ");
            }
        }
        dedent();
        emit();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprName node) {
        emit("Name(", node.value, ")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprCall node) {
        emit("FuncCall(");
        emit();
        indent();
        node.name.accept(this);
        if (node.params.size() == 0) {
            emit("[]");
        }
        else {
            emit(", [");
            emit();
            indent();
            for (int i = 0; i < node.params.size(); i++) {
                ASTExpr param = node.params.get(i);
                param.accept(this);
                if (i != node.params.size() - 1) {
                    emit(", ");
                }
            }
            dedent();
            emit();
            emit("]");
        }
        dedent();
        emit();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtLet node) {
        emit("Let(");
        emit();
        indent();
        node.name.accept(this);
        emit(",");
        emit();
        node.expr.accept(this);
        dedent();
        emit();
        emit(")");
        emit();
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprBlock node) {
        emit("Block(");
        emit();
        indent();
        node.stmts.forEach(stmt -> stmt.accept(this));
        dedent();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprLambda node) {
        emit("Lambda(");
        emit();
        indent();
        emit("[");
        for (int i = 0; i < node.paramDtors.size(); i++) {
            ASTDeconstructor param = node.paramDtors.get(i);
            param.accept(this);
            if (i != node.paramDtors.size() - 1) {
                emit(", ");
            }
        }
        emit("],");
        emit();
        node.body.accept(this);
        emit();
        dedent();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtExpr node) {
        node.expr.accept(this);
        emit();
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtFuncDef node) {
        emit("FuncDef(");
        emit();
        indent();
        emit("[");
        for (int i = 0; i < node.paramDtors.size(); i++) {
            ASTDeconstructor param = node.paramDtors.get(i);
            param.accept(this);
            if (i != node.paramDtors.size() - 1) {
                emit(", ");
            }
        }
        emit("],");
        emit();
        node.body.accept(this);
        emit();
        dedent();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtAssign node) {
        emit("Assign(");
        emit();
        indent();
        node.name.accept(this);
        emit(",");
        emit();
        node.expr.accept(this);
        dedent();
        emit();
        emit(")");
        emit();
        return sb;
    }

    @Override
    public StringBuilder visit(ASTDeconInt node) {
        emit("DeconInt(");
        node.literal.accept(this);
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTDeconName node) {
        emit("DeconString(");
        node.name.accept(this);
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTDeconString node) {
        emit("DeconString(");
        node.value.accept(this);
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTDeconList node) {
        emit("DeconList(");
        indent();
        node.decons.forEach(decon -> decon.accept(this));
        dedent();
        emit(")");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTExprNone node) {
        emit("None()");
        return sb;
    }

    @Override
    public StringBuilder visit(ASTStmtForInLoop node) {
        emit("ForInLoop(");
        emit();
        indent();
        node.variableDecon.accept(this);
        emit(", ");
        emit();
        node.listExpr.accept(this);
        emit(",");
        emit();
        node.body.accept(this);
        emit();
        dedent();
        emit(")");
        emit();
        return sb;
    }
}
