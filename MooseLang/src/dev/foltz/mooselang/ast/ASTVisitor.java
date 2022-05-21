package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.statement.*;
import dev.foltz.mooselang.ast.typing.ASTTypeLiteral;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;

public interface ASTVisitor<T> {
    // == Expressions
    // -- Literals (value and type known)
    T visit(ASTExprNone node);
    T visit(ASTExprBool node);
    T visit(ASTExprInt node);
    T visit(ASTExprString node);
    // -- Named reference (lookup type & value in context)
    T visit(ASTExprName node);

    // == Statements
    // -- Let binding (bind type & value to context)
    T visit(ASTStmtLet node);

    // == Typing
    // -- Named type (lookup type definition in context)
    T visit(ASTTypeName node);
    // -- Union type
    T visit(ASTTypeUnion node);
    // -- Literal type (singleton type of given value)
    T visit(ASTTypeLiteral node);
}
