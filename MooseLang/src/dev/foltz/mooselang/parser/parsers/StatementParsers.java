package dev.foltz.mooselang.parser.parsers;

import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.ASTExprTyped;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.List;

import static dev.foltz.mooselang.parser.ParseResult.failure;
import static dev.foltz.mooselang.parser.parsers.Parsers.*;

public class StatementParsers {
    public static final IParser<List<ASTStmt>> parseProgram = StatementParsers::parseProgram;
    public static final IParser<ASTStmt> parseStmt = StatementParsers::parseStmt;
    public static final IParser<ASTStmtLet> parseStmtLet = StatementParsers::parseStmtLet;
    public static final IParser<ASTStmtFuncDef> parseStmtFuncDef = StatementParsers::parseStmtFuncDef;

    public static ParseResult<List<ASTStmt>> parseProgram(ParseState state) {
        var r = all(parseStmt).map(ls -> (List<ASTStmt>) ls).parse(state);
        if (r.failed()) {
            return failure(r.state, "Failed to parse program: " + r.getMsg());
        }
        return r;
    }

    public static ParseResult<ASTStmt> parseStmt(ParseState state) {
        return any(
            parseStmtLet,
            parseStmtFuncDef
        ).map(stmt -> (ASTStmt) stmt).mapErrorMsg(s -> "parseStmt failed: " + s).parse(state);
    }

    public static ParseResult<ASTStmtLet> parseStmtLet(ParseState state) {
        return sequence(
            expect(TokenType.T_KW_LET),
            any(
                parseExprNameWithType,
                parseExprName
            ),
            expect(TokenType.T_EQUALS),
            parseExpr
        ).map(objs -> {
            var name = objs.get(1);
            var body = (ASTExpr) objs.get(3);
            if (name instanceof ASTExprTyped<?>) {
                return new ASTStmtLet((ASTExprTyped<ASTExprName>) name, body);
            }
            else {
                return new ASTStmtLet((ASTExprName) name, body);
            }
        }).mapErrorMsg(s -> "parseStmtLet failed: " + s).parse(state);
    }

    public static ParseResult<ASTStmtFuncDef> parseStmtFuncDef(ParseState state) {
        return sequence(
            expect(TokenType.T_KW_DEF),
            parseExprName,
            sequence(
                expect("("),
                sepBy(
                    parseExprNameWithType,
                    expect(",")
                ),
                expect(")")
            ).map(objs -> objs.get(1)),
            optional(parseTypeAnnotation),
            expect("="),
            parseExpr
        ).map(objs -> {
            var name = (ASTExprName) objs.get(1);
            var typedParams = (List<ASTExprTyped<ASTExprName>>) objs.get(2);
            var retType = (ASTType) objs.get(3);
            var body = (ASTExpr) objs.get(5);
            return new ASTStmtFuncDef(name, typedParams, retType, body);
        }).parse(state);
    }
}
