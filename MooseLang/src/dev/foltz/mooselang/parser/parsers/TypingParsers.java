package dev.foltz.mooselang.parser.parsers;

import dev.foltz.mooselang.ast.expression.ASTExprName;
import dev.foltz.mooselang.ast.expression.ASTExprTyped;
import dev.foltz.mooselang.ast.typing.*;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.parser.ParseState;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.foltz.mooselang.parser.parsers.ExpressionParsers.*;
import static dev.foltz.mooselang.parser.parsers.ExpressionParsers.parseExprNameWithType;
import static dev.foltz.mooselang.parser.parsers.ParserCombinators.*;

public class TypingParsers {
    public static final IParser<ASTType> parseTypeTopLevel = TypingParsers::parseTypeTopLevel;
    public static final IParser<ASTType> parseTypeSimple = TypingParsers::parseTypeSimple;
    public static final IParser<ASTTypeName> parseTypeName = TypingParsers::parseTypeName;
    public static final IParser<ASTTypeUnion> parseTypeUnion = TypingParsers::parseTypeUnion;
    public static final IParser<ASTTypeRecord> parseTypeRecord = TypingParsers::parseTypeRecord;
    public static final IParser<ASTType> parseTypeLiteral = TypingParsers::parseTypeLiteral;

    public static ParseResult<ASTTypeName> parseTypeName(ParseState state) {
        return parseExprName.map(name -> new ASTTypeName(name.name())).parse(state);
    }

    public static ParseResult<ASTTypeUnion> parseTypeUnion(ParseState state) {
        return sepBy2(
            any(parseTypeSimple, parseTypeRecord).map(t -> (ASTType) t),
            expect("|")
        ).map(types -> new ASTTypeUnion((List<ASTType>) types)).parse(state);
    }

    public static ParseResult<ASTTypeRecord> parseTypeRecord(ParseState state) {
        return sequence(
            expect("{"),
            sepBy1(
                parseExprNameWithType,
                expect(",")
            ),
            expect("}")
        ).map(objs -> {
            var typedNames = (List<ASTExprTyped<ASTExprName>>) objs.get(1);
            var fieldTypes = typedNames.stream()
                .map(tn -> new AbstractMap.SimpleEntry<>(tn.expr, tn.type));
            return new ASTTypeRecord(fieldTypes.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }).parse(state);
    }

    public static ParseResult<ASTType> parseTypeLiteral(ParseState state) {
        return any(
            parseExprNone.map(none -> new ASTTypeName("None")),
            parseExprBool.map(ASTTypeValue::new),
            parseExprInt.map(ASTTypeValue::new),
            parseExprString.map(ASTTypeValue::new)
        ).map(t -> (ASTType) t).parse(state);
    }

    public static ParseResult<ASTType> parseTypeSimple(ParseState state) {
        return any(
            parseTypeName,
            parseTypeLiteral
        ).map(t -> (ASTType) t).parse(state);
    }

    public static ParseResult<ASTType> parseTypeTopLevel(ParseState state) {
        return any(
            parseTypeUnion,
            parseTypeRecord,
            parseTypeSimple
        ).map(t -> (ASTType) t).parse(state);
    }
}
