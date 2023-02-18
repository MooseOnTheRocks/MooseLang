package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeName;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeSum;
import dev.foltz.mooselang.ast.nodes.type.ASTTypeTuple;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final Parser<ASTTypeSum> typeSum =
        joining(all(anyws, match("|"), anyws), all(name, many(type)))
        .mapState(s -> {
            if (s.isError) {
                return s.error(s.error);
            }
            var ls = s.result;
            var names = new ArrayList<String>();
            var typeParams = new ArrayList<List<ASTType>>();
            for (var entry : ls) {
                var name = (String) entry.get(0);
                var params = (List<ASTType>) entry.get(1);
                if (names.contains(name)) {
                    return s.error("Redefinition of constructor name in sum-type: " + name);
                }
                names.add(name);
                typeParams.add(List.copyOf(params));
            }

            return s.success(s.index, new ASTTypeSum(names, typeParams));
        });

    private static ParserState<ASTType> parseType(ParserState<?> s) {
        return any(
            typeName,
            typeTuple)
        .map(t -> (ASTType) t).run(s);
    }
}
