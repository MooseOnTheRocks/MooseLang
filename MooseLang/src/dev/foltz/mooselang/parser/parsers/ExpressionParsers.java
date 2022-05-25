package dev.foltz.mooselang.parser.parsers;

import dev.foltz.mooselang.ast.expression.*;
import dev.foltz.mooselang.ast.expression.literals.*;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.foltz.mooselang.parser.parsers.Parsers.*;

public class ExpressionParsers {
    public static final IParser<ASTExpr> parseExpr = ExpressionParsers::parseExpr;
    public static final IParser<ASTExpr> parseExprSimple = ExpressionParsers::parseExprSimple;
    public static final IParser<ASTExpr> parseExprParen = ExpressionParsers::parseExprParen;
    public static final IParser<ASTExprNone> parseExprNone = ExpressionParsers::parseExprNone;
    public static final IParser<ASTExprBool> parseExprBool = ExpressionParsers::parseExprBool;
    public static final IParser<ASTExprInt> parseExprInt = ExpressionParsers::parseExprInt;
    public static final IParser<ASTExprString> parseExprString = ExpressionParsers::parseExprString;
    public static final IParser<ASTExprName> parseExprName = ExpressionParsers::parseExprName;
    public static final IParser<ASTExprTyped<ASTExprName>> parseExprNameWithType = ExpressionParsers::parseExprNameWithType;
    public static final IParser<ASTExprCall> parseExprBinOp = ExpressionParsers::parseExprBinOp;
    public static final IParser<ASTExprCall> parseExprCall = ExpressionParsers::parseExprCall;
    public static final IParser<ASTExprRecord> parseExprRecord = ExpressionParsers::parseExprRecord;

    public static final IParser<ASTType> parseTypeAnnotation = ExpressionParsers::parseTypeAnnotation;

    public static final IParser<ASTExprIfThenElse> parseExprIfThenElse = ExpressionParsers::parseExprIfThenElse;


    public static ParseResult<ASTExprNone> parseExprNone(ParseState state) {
        return expect(TokenType.T_NAME, "None").map(t -> new ASTExprNone()).parse(state);
    }

    public static ParseResult<ASTExprBool> parseExprBool(ParseState state) {
        return any(
            expect(TokenType.T_TRUE).map(t -> new ASTExprBool(true)),
            expect(TokenType.T_FALSE).map(t -> new ASTExprBool(false))
        ).map(b -> (ASTExprBool) b).parse(state);
    }

    public static ParseResult<ASTExprInt> parseExprInt(ParseState state) {
        return expect(TokenType.T_NUMBER)
            .map(t -> {
                var s = t.value;
                int value;
                if (s.startsWith("0x")) {
                    value = Integer.parseInt(s.substring(2), 16);
                }
                else if (s.startsWith("0b")) {
                    value = Integer.parseInt(s.substring(2), 2);
                }
                else {
                    value = Integer.parseInt(s, 10);
                }
                return new ASTExprInt(value);
            }).parse(state);
    }

    public static ParseResult<ASTExprString> parseExprString(ParseState state) {
        return any(expect(TokenType.T_STRING), expect(TokenType.T_CHAR))
            .map(t -> new ASTExprString(((Token) t).value)).parse(state);
    }

    public static ParseResult<ASTExprName> parseExprName(ParseState state) {
        return any(
                expect(TokenType.T_NAME),
                expect(TokenType.T_NAME_SYMBOLIC)
            ).map(t -> (Token) t)
            .map(t -> new ASTExprName(t.value)).parse(state);
    }

    public static ParseResult<ASTExprTyped<ASTExprName>> parseExprNameWithType(ParseState state) {
        return sequence(
            parseExprName,
            parseTypeAnnotation
        ).map(objs -> {
            var name = (ASTExprName) objs.get(0);
            var type = (ASTType) objs.get(1);
            return new ASTExprTyped<>(name, type);
        }).parse(state);
    }

    public static ParseResult<ASTExpr> parseExprParen(ParseState state) {
        return sequence(
            expect("("),
            parseExpr,
            expect(")")
        ).map(objs -> (ASTExpr) objs.get(1)).parse(state);
    }

    public static ParseResult<ASTExprCall> parseExprCall(ParseState state) {
        return sequence(
            parseExprName,
            expect("("),
            sepBy(
                parseExpr,
                expect(",")
            ),
            expect(")")
        ).map(objs -> {
            var name = (ASTExprName) objs.get(0);
            var params = (List<ASTExpr>) objs.get(2);
            return new ASTExprCall(name, params);
        }).parse(state);
    }

    public static ParseResult<ASTExprCall> parseExprBinOp(ParseState state) {
        return sequence(
            parseExprSimple,
            parseExprName,
            parseExpr
        ).map(objs -> new ASTExprCall((ASTExprName) objs.get(1), List.of((ASTExpr) objs.get(0), (ASTExpr) objs.get(2)))).parse(state);
    }

    public static ParseResult<ASTExpr> parseExpr(ParseState state) {
        return any(parseExprBinOp, parseExprSimple).map(obj -> (ASTExpr) obj).parse(state);
    }

    public static ParseResult<ASTExpr> parseExprSimple(ParseState state) {
        var parseAnyExpr = any(
            parseExprNone,
            parseExprBool,
            parseExprInt,
            parseExprString,
            parseExprRecord,
            parseExprCall,
            parseExprName,
            parseExprIfThenElse,
            parseExprParen
        ).map(expr -> (ASTExpr) expr);

        return sequence(parseAnyExpr, optional(parseTypeAnnotation)).map(objs -> {
            var expr = (ASTExpr) objs.get(0);
            var type = objs.get(1);
            if (type instanceof ASTType astType) {
                return new ASTExprTyped<>(expr, astType);
            }
            else {
                return expr;
            }
        }).mapErrorMsg(s -> "parseExpr failed: " + s).parse(state);
    }

    public static ParseResult<ASTType> parseTypeAnnotation(ParseState state) {
        var parseAnnotation = sequence(
                expect(":"),
                parseTypeTopLevel
        ).map(objs -> (ASTType) objs.get(1));
        return parseAnnotation.parse(state);
    }

    public static ParseResult<ASTExprIfThenElse> parseExprIfThenElse(ParseState state) {
        return sequence(
            expect(TokenType.T_KW_IF),
            parseExpr,
            expect(TokenType.T_KW_THEN),
            parseExpr,
            expect(TokenType.T_KW_ELSE),
            parseExpr
        ).map(objs -> {
            var predicate = (ASTExpr) objs.get(1);
            var exprTrue = (ASTExpr) objs.get(3);
            var exprFalse = (ASTExpr) objs.get(5);
            return new ASTExprIfThenElse(predicate, exprTrue, exprFalse);
        }).parse(state);
    }

    public static ParseResult<ASTExprRecord> parseExprRecord(ParseState state) {
        return sequence(
            expect("new"),
            expect("{"),
            sepBy1(
                sequence(
                    parseExprName,
                    expect("="),
                    parseExpr
                ).map(objs -> List.of(objs.get(0), objs.get(2))),
                expect(",")
            ),
            expect("}")
        ).map(objs -> {
            var nameValues = (List<List<ASTExpr>>) objs.get(2);
            var fields = nameValues.stream().map(tn -> new AbstractMap.SimpleEntry<>((ASTExprName) tn.get(0), (ASTExpr) tn.get(1)));
            return new ASTExprRecord(fields.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }).parse(state);
    }
}
