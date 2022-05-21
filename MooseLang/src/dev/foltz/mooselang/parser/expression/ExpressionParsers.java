package dev.foltz.mooselang.parser.expression;

import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.literals.ASTExprBool;
import dev.foltz.mooselang.ast.expression.literals.ASTExprInt;
import dev.foltz.mooselang.ast.expression.literals.ASTExprNone;
import dev.foltz.mooselang.ast.expression.literals.ASTExprString;
import dev.foltz.mooselang.ast.typing.ASTType;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import static dev.foltz.mooselang.parser.Parsers.*;

public class ExpressionParsers {
    public static final IParser<ASTExpr> parseExpr = ExpressionParsers::parseExpr;
    public static final IParser<ASTExprNone> parseExprNone = ExpressionParsers::parseExprNone;
    public static final IParser<ASTExprBool> parseExprBool = ExpressionParsers::parseExprBool;
    public static final IParser<ASTExprInt> parseExprInt = ExpressionParsers::parseExprInt;
    public static final IParser<ASTExprString> parseExprString = ExpressionParsers::parseExprString;
    public static final IParser<ASTExprName> parseExprName = ExpressionParsers::parseExprName;
    public static final IParser<ASTExprName> parseExprNameWithType = ExpressionParsers::parseExprNameWithType;

    public static final IParser<ASTType> parseTypeAnnotation = ExpressionParsers::parseTypeAnnotation;

    public static ParseResult<ASTExprNone> parseExprNone(ParseState state) {
        return expect(TokenType.T_NAME, "None").map(t -> new ASTExprNone()).parse(state);
    }

    public static ParseResult<ASTExprBool> parseExprBool(ParseState state) {
        return any(
            expect(TokenType.T_NAME, "True").map(t -> new ASTExprBool(true)),
            expect(TokenType.T_NAME, "False").map(t -> new ASTExprBool(false))
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
        return expect(TokenType.T_NAME).map(t -> new ASTExprName(t.value)).parse(state);
    }

    public static ParseResult<ASTExprName> parseExprNameWithType(ParseState state) {
        return sequence(
            parseExprName,
            parseTypeAnnotation
        ).map(objs -> {
            var name = (ASTExprName) objs.get(0);
            var type = (ASTType) objs.get(1);
            return (ASTExprName) name.withTypeHint(type);
        }).parse(state);
    }

    public static ParseResult<ASTExpr> parseExpr(ParseState state) {
        var parseAnyExpr = any(
            parseExprNone,
            parseExprBool,
            parseExprInt,
            parseExprString,
            parseExprName
        ).map(expr -> (ASTExpr) expr);

        return sequence(parseAnyExpr, optional(parseTypeAnnotation)).map(objs -> {
            var expr = (ASTExpr) objs.get(0);
            var type = objs.get(1);
            if (type instanceof ASTType astType) {
                return expr.withTypeHint(astType);
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
}
