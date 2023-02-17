package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeName;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeTuple;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;

import java.util.List;

import static dev.foltz.mooselang.parser.ParserCombinators.*;
import static dev.foltz.mooselang.parser.Parsers.*;

public class ParsersASTType {
    public static final Parser<ASTType> type = ParsersASTType::parseType;
    public static final Parser<ASTTypeName> typeName = any(name, match("()")).map(s -> (String) s).map(ASTTypeName::new);
    public static final Parser<ASTTypeTuple> typeTuple =
        intersperse(anyws,
            match("("),
            joining(all(anyws, match(","), anyws), type),
            match(")"))
        .map(ls -> new ASTTypeTuple((List<ASTType>) ls.get(1)));

    private static ParserState<ASTType> parseType(ParserState<?> s) {
        return any(
            typeName,
            typeTuple)
        .map(t -> (ASTType) t).run(s);
    }
}
