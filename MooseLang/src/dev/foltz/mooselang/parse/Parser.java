package dev.foltz.mooselang.parse;

import dev.foltz.mooselang.token.Token;
import dev.foltz.mooselang.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Parser {
    public static final Map<ASTType, Function<CharSequence, Integer>> AST_PARSERS = Map.ofEntries(

    );

    private List<Token> remainder;

    public Parser() {
        this.remainder = new ArrayList<>();
    }

    public Parser feed(Token token) {
        remainder.add(token);
        return this;
    }

    public AST nextNode() {

    }
}
