package dev.foltz.mooselang.token;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static dev.foltz.mooselang.token.TokenType.*;
import static java.util.Map.entry;


public class Tokenizer {
    public static final Map<TokenType, Function<CharSequence, Integer>> TOKEN_PARSERS = Map.ofEntries(
            entry(T_NEWLINE, buildSpan(Tokenizer::isNewline)),
            entry(T_WHITESPACE, buildSpan(Tokenizer::isWhitespace)),

            entry(T_NAME, buildSpan(Tokenizer::isAlpha, ((Predicate<Character>) Tokenizer::isAlpha).or(Tokenizer::isNum))),
            entry(T_NUMBER, buildSpan(Tokenizer::isNum)),

            entry(T_EQUALS, buildMatch("=")),
            entry(T_COMMA, buildMatch(",")),

            entry(T_LPAREN, buildMatch("(")),
            entry(T_RPAREN, buildMatch(")")),
            entry(T_LBRACE, buildMatch("{")),
            entry(T_RBRACE, buildMatch("}"))
    );

    private StringBuffer remainder;

    public Tokenizer() {
        remainder = new StringBuffer();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (!isEmpty()) {
            Token token = nextToken();
            tokens.add(token);
        }
        return tokens;
    }

    public Token nextToken() {
        if (isEmpty()) {
            throw new IllegalStateException("Failed to parse next token: input is empty.");
        }

        for (Map.Entry<TokenType, Function<CharSequence, Integer>> tokenParser : TOKEN_PARSERS.entrySet()) {
            int index = tokenParser.getValue().apply(remainder);
            if (index > 0) {
                String capture = remainder.substring(0, index);
                remainder.delete(0, index);
                return new Token(tokenParser.getKey(), capture);
            }
        }

        throw new IllegalStateException("Failed to parse next token: illegal character '" + remainder.charAt(0) + "'");
    }

    public Tokenizer feed(String source) {
        remainder.append(source).append("\n");
        return this;
    }

    public boolean isEmpty() {
        return remainder.isEmpty();
    }

    public static boolean isNewline(char c) {
        return "\n".contains("" + c);
    }

    public static boolean isWhitespace(char c) {
        return " \t\r\n".contains("" + c);
    }

    public static boolean isAlpha(char c) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".contains("" + c);
    }

    public static boolean isNum(char c) {
        return "0123456789".contains("" + c);
    }

    public static Function<CharSequence, Integer> buildSpan(Predicate<Character> predInitial, Predicate<Character> predGeneral) {
        return source -> {
            if (source.isEmpty()) {
                return 0;
            }

            int index = 0;
            char c = source.charAt(index);
            if (predInitial.test(c)) {
                while (predGeneral.test(c)) {
                    index += 1;
                    if (index >= source.length()) {
                        break;
                    }
                    c = source.charAt(index);
                }
            }
            return index;
        };
    }

    public static Function<CharSequence, Integer> buildSpan(Predicate<Character> pred) {
        return buildSpan(pred, pred);
    }

    public static Function<CharSequence, Integer> buildMatch(String match) {
        return source -> {
            if (source.isEmpty()) {
                return 0;
            }
            return CharSequence.compare(source.subSequence(0, match.length()), match) == 0 ? match.length() : 0;
        };
    }
}
