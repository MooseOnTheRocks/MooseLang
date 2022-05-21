package dev.foltz.mooselang.parser.parsers;

import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprString;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.ast.typing.ASTTypeLiteral;
import dev.foltz.mooselang.ast.typing.ASTTypeName;
import dev.foltz.mooselang.ast.typing.ASTTypeUnion;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.typing.type.builtin.BuiltinTypes;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.ParseResult.failure;
import static dev.foltz.mooselang.parser.ParseResult.success;
import static dev.foltz.mooselang.parser.parsers.ExpressionParsers.parseExprBool;
import static dev.foltz.mooselang.parser.parsers.ExpressionParsers.parseExprNone;

public class Parsers {
    public static final IParser<List<ASTStmt>> parseProgram = StatementParsers.parseProgram;
    public static final IParser<ASTStmt> parseStmt = StatementParsers.parseStmt;
    public static final IParser<ASTStmtLet> parseStmtLet = StatementParsers.parseStmtLet;

    public static final IParser<ASTExpr> parseExpr = ExpressionParsers.parseExpr;
    public static final IParser<ASTExprInt> parseExprInt = ExpressionParsers.parseExprInt;
    public static final IParser<ASTExprString> parseExprString = ExpressionParsers.parseExprString;
    public static final IParser<ASTExprName> parseExprName = ExpressionParsers.parseExprName;
    public static final IParser<ASTExprName> parseExprNameWithType = ExpressionParsers.parseExprNameWithType;
    public static final IParser<ASTType> parseTypeAnnotation = ExpressionParsers.parseTypeAnnotation;

    public static final IParser<ASTType> parseTypeTopLevel = Parsers::parseTypeTopLevel;
    public static final IParser<ASTType> parseType = Parsers::parseType;
    public static final IParser<ASTTypeName> parseTypeName = Parsers::parseTypeName;
    public static final IParser<ASTTypeUnion> parseTypeUnion = Parsers::parseTypeUnion;
    public static final IParser<ASTType> parseTypeLiteral = Parsers::parseTypeLiteral;

    public static ParseResult<ASTTypeName> parseTypeName(ParseState state) {
        return parseExprName.map(name -> new ASTTypeName(name.name)).parse(state);
    }

    public static ParseResult<ASTTypeUnion> parseTypeUnion(ParseState state) {
        return sepBy1(parseType, expect("|")).map(types -> new ASTTypeUnion((List<ASTType>) types)).parse(state);
    }

    public static ParseResult<ASTType> parseTypeLiteral(ParseState state) {
        return any(
            parseExprNone.map(n -> BuiltinTypes.TYPE_NONE),
            parseExprBool.map(ASTTypeLiteral::new),
            parseExprInt.map(ASTTypeLiteral::new),
            parseExprString.map(ASTTypeLiteral::new)
        ).map(t -> (ASTType) t).parse(state);
    }

    public static ParseResult<ASTType> parseType(ParseState state) {
        return any(
            parseTypeName,
            parseTypeLiteral
        ).map(t -> (ASTType) t).parse(state);
    }

    public static ParseResult<ASTType> parseTypeTopLevel(ParseState state) {
        return any(
            parseTypeUnion,
            parseType
        ).map(t -> (ASTType) t).parse(state);
    }

    public static IParser<Token> expect(TokenType type, String value) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + type + ", " + value + ") failed, state is empty");
            }

            Token token = state.peek();
            if (token.type == type && token.value.equals(value)) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + type + ", " + value + ") failed, next token is " + token);
        };
    }

    public static IParser<Token> expect(String value) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + value + ") failed, state is empty.");
            }

            Token token = state.peek();
            if (value.equals(token.value)) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + value +") failed, next token is " + token);
        };
    }

    public static IParser<Token> expect(TokenType type) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + type.name() + ") failed, state is empty.");
            }

            Token token = state.peek();
            if (token.type == type) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + type.name() + ") failed, next token is " + token);
        };
    }

    public static <T> IParser<T> optional(IParser<T> p) {
        return state -> {
            var r = p.parse(state);
            return r.isSuccess() ? r : success(state, null);
        };
    }

    public static IParser<List<?>> sequence(IParser<?> ...parsers) {
        return state -> {
            List<Object> results = new ArrayList<>();
            ParseResult<?> r = success(state, null);
            for (IParser<?> p : parsers) {
                r = p.parse(r.state);
                if (r.failed()) {
                    return failure(r.state, "sequence(...) failed: " + r.getMsg());
                }
                results.add(r.get());
            }
            return success(r.state, results);
        };
    }

    public static IParser<Object> any(IParser<?> ...parsers) {
        return state -> {
            for (IParser<?> p : parsers) {
                var r = p.parse(state);
                if (r.isSuccess()) {
                    return success(r.state, r.get());
                }
            }
            return failure(state, "any(...) failed");
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

    public static IParser<List<?>> all(IParser<?> parser) {
        return state -> {
            List<Object> results = new ArrayList<>();
            var r = parser.parse(state);
            while (r.isSuccess()) {
                results.add(r.get());
                r = parser.parse(r.state);
            }
            if (r.failed() && r.state.isEmpty()) {
                return success(r.state, results);
            }
            return failure(r.state, "all(...) failed: " + r.getMsg());
        };
    }

    public static IParser<List<?>> many1(IParser<?> parser) {
        return sequence(parser, many(parser)).map(objs -> {
            List<Object> ls = new ArrayList<>();
            ls.add(objs.get(0));
            ls.addAll((List<?>) objs.get(1));
            return ls;
        });
    }

    public static <T> IParser<List<T>> sepBy1(IParser<T> parser, IParser<?> sep) {
        var sepThenP = sequence(sep, parser).map(objs -> (T) objs.get(1));
        return sequence(parser, many1(sepThenP))
            .map(objs -> {
                List<T> results = new ArrayList<>();
                results.add((T) objs.get(0));
                results.addAll((List<T>) objs.get(1));
                return results;
            });
    }

    public static <T> IParser<List<T>> sepBy(IParser<T> parser, IParser<?> sep) {
        var sepThenP = sequence(sep, parser).map(objs -> (T) objs.get(1));
        return sequence(parser, many(sepThenP))
            .map(objs -> {
                List<T> results = new ArrayList<>();
                results.add((T) objs.get(0));
                results.addAll((List<T>) objs.get(1));
                return results;
            });
    }
}
