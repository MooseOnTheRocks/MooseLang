package dev.foltz.mooselang.tokenizer;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static dev.foltz.mooselang.tokenizer.TokenType.*;
import static java.util.Map.entry;


public class Tokenizer {
    public static final Map<TokenType, Function<CharSequence, Integer>> TOKEN_PARSERS = new LinkedHashMap<>();
    static {
        TOKEN_PARSERS.put(T_NEWLINE, buildSpan(Tokenizer::isNewline));
        TOKEN_PARSERS.put(T_WHITESPACE, buildSpan(Tokenizer::isWhitespace));

//        TOKEN_PARSERS.put(T_KW_LET, buildMatch("let"));
//        TOKEN_PARSERS.put(T_KW_DEF, buildMatch("def"));
//        TOKEN_PARSERS.put(T_KW_FOR, buildMatch("for"));
//        TOKEN_PARSERS.put(T_KW_IN, buildMatch("in"));

        TOKEN_PARSERS.put(T_COMMENT, Tokenizer::matchComment);
        TOKEN_PARSERS.put(T_NONE, buildMatch("None"));
        TOKEN_PARSERS.put(T_NUMBER, buildSpan(Tokenizer::isNum));
        TOKEN_PARSERS.put(T_STRING, Tokenizer::matchString);
        TOKEN_PARSERS.put(T_NAME, buildSpan(
                ((Predicate<Character>) Tokenizer::isAlpha).or(c -> "_#$'".contains("" + c)),
                ((Predicate<Character>) Tokenizer::isAlpha).or(Tokenizer::isNum).or(c -> "_#$'".contains("" + c))));

        TOKEN_PARSERS.put(T_ELLIPSES, buildMatch(".."));
        TOKEN_PARSERS.put(T_FAT_ARROW, buildMatch("=>"));

        TOKEN_PARSERS.put(T_MINUS, buildMatch("-"));
        TOKEN_PARSERS.put(T_DOT, buildMatch("."));
        TOKEN_PARSERS.put(T_EQUALS, buildMatch("="));
        TOKEN_PARSERS.put(T_COMMA, buildMatch(","));

        TOKEN_PARSERS.put(T_LPAREN, buildMatch("("));
        TOKEN_PARSERS.put(T_RPAREN, buildMatch(")"));
        TOKEN_PARSERS.put(T_LBRACE, buildMatch("{"));
        TOKEN_PARSERS.put(T_RBRACE, buildMatch("}"));
        TOKEN_PARSERS.put(T_LBRACKET, buildMatch("["));
        TOKEN_PARSERS.put(T_RBRACKET, buildMatch("]"));
    }

    private final StringBuffer remainder;

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
                String capture = tokenParser.getKey() == T_STRING
                        ? remainder.substring(1, index - 1)
                        : remainder.substring(0, index);
                remainder.delete(0, index);
                return switch (capture) {
                    case "let" -> new Token(T_KW_LET, capture);
                    case "def" -> new Token(T_KW_DEF, capture);
                    case "for" -> new Token(T_KW_FOR, capture);
                    case "in" -> new Token(T_KW_IN, capture);
                    case "do" -> new Token(T_KW_DO, capture);
                    case "lambda" -> new Token(T_KW_LAMBDA, capture);
                    default -> new Token(tokenParser.getKey(), capture);
                };
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
            if (source.isEmpty() || source.length() < match.length()) {
                return 0;
            }
//            System.out.println("match = " + match);
            return CharSequence.compare(source.subSequence(0, match.length()), match) == 0 ? match.length() : 0;
        };
    }

    public static int matchComment(CharSequence source) {
        if (source.isEmpty() || source.length() < 2) {
            return 0;
        }

        if (source.subSequence(0, 2).equals("//")) {
            int index = 2;
            while(index < source.length()) {
                char c = source.charAt(index);
                if (c == '\n') {
                    return index;
                }
                index++;
            }
            return index;
        }

        return 0;
    }

    public static int matchString(CharSequence source) {
        if (source.isEmpty()) {
            return 0;
        }

        if (source.charAt(0) != '"') {
            return 0;
        }

        int index = 1;
        while (true) {
            char c = source.charAt(index);
            index += 1;
            if (index >= source.length()) {
                return 0;
            }
            if (c == '"') {
                break;
            }
        }
        return index;
    }
}
