package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprList;
import dev.foltz.mooselang.ast.expression.literals.ASTExprString;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.ParseResult.failure;
import static dev.foltz.mooselang.parser.ParseResult.success;

public class Parsers {

    public static final IParser<ASTExprInt> parseExprInt = Parsers::parseExprInt;
    public static final IParser<ASTExprString> parseExprString = Parsers::parseExprString;
    public static final IParser<ASTExprName> parseExprName = Parsers::parseExprName;
    public static final IParser<ASTExprList> parseExprList = Parsers::parseExprList;
    public static final IParser<ASTExpr> parseExpr = Parsers::parseExpr;

    public static final IParser<ASTStmtLet> parseStmtLet = Parsers::parseStmtLet;
    public static final IParser<ASTStmt> parseStmt = Parsers::parseStmt;

    public static IParser<Token> expect(TokenType type) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state);
            }

            Token token = state.peek();
            if (token.type == type) {
                return success(state.next(), token);
            }

            return failure(state);
        };
    }

    public static IParser<List<?>> sequence(List<IParser<?>> parsers) {
        return state -> {
            List<Object> results = new ArrayList<>();
            ParseResult<?> r = success(state, null);
            for (IParser<?> p : parsers) {
                r = p.parse(r.state);
                if (r.failed()) {
                    return failure(r.state);
                }
                results.add(r.get());
            }
            return success(r.state, results);
        };
    }

    public static IParser<Object> any(List<IParser<?>> parsers) {
        return state -> {
            for (IParser<?> p : parsers) {
                var r = p.parse(state);
                if (r.isSuccess()) {
                    return success(r.state, r.get());
                }
            }
            return failure(state);
        };
    }

    public static IParser<List<?>> many(IParser<?> parser) {
        return state -> {
            List<Object> results = new ArrayList<>();
            var r = parser.parse(state);
            while (r.isSuccess()) {
                results.add(r.get());
                r = parser.parse(r.state);
            }
            return success(r.state, results);
        };
    }

    public static ParseResult<ASTExprInt> parseExprInt(ParseState state) {
        return expect(TokenType.T_NUMBER).parse(state)
                .map(t -> new ASTExprInt(Integer.parseInt(t.value)));
    }

    public static ParseResult<ASTExprString> parseExprString(ParseState state) {
        return expect(TokenType.T_STRING).parse(state)
                .map(t -> new ASTExprString(t.value));
    }

    public static ParseResult<ASTExprName> parseExprName(ParseState state) {
        return expect(TokenType.T_NAME).parse(state)
                .map(t -> new ASTExprName(t.value));
    }

    public static ParseResult<ASTExprList> parseExprList(ParseState state) {
        var r1 = expect(TokenType.T_LBRACKET).parse(state);
        if (r1.failed()) {
            return failure(r1.state);
        }

        List<ASTExpr> exprs = new ArrayList<>();
        var nextExpr = parseExpr(r1.state);
        if (nextExpr.isSuccess()) {
            exprs.add(nextExpr.get());
            var comma = expect(TokenType.T_COMMA).parse(nextExpr.state);
            do {
                nextExpr = parseExpr(comma.state);
                if (nextExpr.failed()) {
                    return failure(nextExpr.state);
                }
                exprs.add(nextExpr.get());
                comma = expect(TokenType.T_COMMA).parse(nextExpr.state);
            } while (comma.isSuccess());
        }

        var r2 = expect(TokenType.T_RBRACKET).parse(nextExpr.failed() ? r1.state : nextExpr.state);
        if (r2.failed()) {
            return failure(r2.state);
        }

        return success(r2.state, new ASTExprList(exprs));
    }

    public static ParseResult<ASTExpr> parseExpr(ParseState state) {
        List<IParser<?>> exprs = List.of(
                parseExprInt,
                parseExprString,
                parseExprList
        );
        return any(exprs).parse(state).map(expr -> (ASTExpr) expr);
    }

    public static ParseResult<ASTStmtLet> parseStmtLet(ParseState state) {
        return sequence(List.of(
                expect(TokenType.T_KW_LET),
                parseExprName,
                expect(TokenType.T_EQUALS),
                parseExpr
        )).map(objs -> {
            var name = (ASTExprName) objs.get(1);
            var expr = (ASTExpr) objs.get(3);
            return new ASTStmtLet(name, expr);
        }).parse(state);
    }

    public static ParseResult<ASTStmt> parseStmt(ParseState state) {
        List<IParser<?>> stmts = List.of(
                parseStmtLet
        );

        return any(stmts).parse(state).map(stmt -> (ASTStmt) stmt);
    }
}
