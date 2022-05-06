package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParseState {
    protected final List<Token> tokensParsed;
    protected final List<Token> tokensRemaining;

    public ParseState(List<Token> parsed, List<Token> remaining) {
        this.tokensParsed = List.copyOf(parsed);
        this.tokensRemaining = List.copyOf(remaining);
    }

    public boolean isEmpty() {
        return tokensRemaining.isEmpty();
    }

    public Token peek() {
        return tokensRemaining.get(0);
    }

    public ParseState next() {
        Token token = tokensRemaining.get(0);
        List<Token> ts = new ArrayList<>(tokensParsed);
        ts.add(token);
        return new ParseState(ts, tokensRemaining.subList(1, tokensRemaining.size()));
    }

    @Override
    public String toString() {
        return "ParseState{" +
                "tokensParsed=" + tokensParsed +
                ", tokensRemaining=" + tokensRemaining +
                '}';
    }
}
