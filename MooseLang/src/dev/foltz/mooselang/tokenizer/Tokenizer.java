package dev.foltz.mooselang.tokenizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static dev.foltz.mooselang.tokenizer.TokenType.*;


public class Tokenizer {
    public static final Map<TokenType, Function<CharSequence, Integer>> TOKEN_PARSERS = new LinkedHashMap<>();

    static {
        TOKEN_PARSERS.put(T_NEWLINE, buildSpan(Tokenizer::isNewline));
        TOKEN_PARSERS.put(T_WHITESPACE, buildSpan(Tokenizer::isWhitespace));

        TOKEN_PARSERS.put(T_COMMENT, Tokenizer::matchComment);
        TOKEN_PARSERS.put(T_NUMBER, Tokenizer::matchNumber);
        TOKEN_PARSERS.put(T_CHAR, Tokenizer::matchChar);
        TOKEN_PARSERS.put(T_STRING, Tokenizer::matchString);
        TOKEN_PARSERS.put(T_NAME, buildSpan(
                ((Predicate<Character>) Tokenizer::isAlpha).or(c -> "_#$'".contains("" + c)),
                ((Predicate<Character>) Tokenizer::isAlpha).or(Tokenizer::isNum).or(c -> "_#$'".contains("" + c))));

        TOKEN_PARSERS.put(T_ELLIPSES, buildMatch(".."));
        TOKEN_PARSERS.put(T_FAT_ARROW, buildMatch("=>"));
        TOKEN_PARSERS.put(T_COLON, buildMatch(":"));

        TOKEN_PARSERS.put(T_MINUS, buildMatch("-"));
        TOKEN_PARSERS.put(T_DOT, buildMatch("."));
        TOKEN_PARSERS.put(T_EQUALS, buildMatch("="));
        TOKEN_PARSERS.put(T_COMMA, buildMatch(","));
        TOKEN_PARSERS.put(T_BAR, buildMatch("|"));

        TOKEN_PARSERS.put(T_LPAREN, buildMatch("("));
        TOKEN_PARSERS.put(T_RPAREN, buildMatch(")"));
        TOKEN_PARSERS.put(T_LBRACE, buildMatch("{"));
        TOKEN_PARSERS.put(T_RBRACE, buildMatch("}"));
        TOKEN_PARSERS.put(T_LBRACKET, buildMatch("["));
        TOKEN_PARSERS.put(T_RBRACKET, buildMatch("]"));
    }

    private final StringBuffer remainder;
    private int consumed = 0;

    public Tokenizer() {
        remainder = new StringBuffer();
    }

    public static Stream<Token> tokenize(String source) {
        return new Tokenizer().feed(source).tokenize().stream();
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
                int from = consumed;
                consumed += index;
                int to = consumed;
                String sourceMatch = remainder.substring(0, index);
                String capture = switch(tokenParser.getKey()) {
                    case T_STRING -> remainder.substring(1, index - 1);
                    case T_CHAR -> remainder.substring(1, index);
                    default -> remainder.substring(0, index);
                };
                remainder.delete(0, index);
                return switch (capture) {
                    case "let" -> new Token(T_KW_LET, capture, from, to, sourceMatch);
                    case "def" -> new Token(T_KW_DEF, capture, from, to, sourceMatch);
                    case "for" -> new Token(T_KW_FOR, capture, from, to, sourceMatch);
                    case "in" -> new Token(T_KW_IN, capture, from, to, sourceMatch);
                    case "do" -> new Token(T_KW_DO, capture, from, to, sourceMatch);
                    case "lambda" -> new Token(T_KW_LAMBDA, capture, from, to, sourceMatch);
                    case "if" -> new Token(T_KW_IF, capture, from, to, sourceMatch);
                    case "then" -> new Token(T_KW_THEN, capture, from, to, sourceMatch);
                    case "else" -> new Token(T_KW_ELSE, capture, from, to, sourceMatch);
                    case "type" -> new Token(T_KW_TYPE, capture, from, to, sourceMatch);
                    default -> new Token(tokenParser.getKey(), capture, from, to, sourceMatch);
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

    public static int matchChar(CharSequence source) {
        if (source.isEmpty()) {
            return 0;
        }

        if (source.charAt(0) != '\'') {
            return 0;
        }

        if (source.length() >= 2) {
            return 2;
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

    public static int matchBinNumber(CharSequence source) {
        if (source.length() < 3 || !source.subSequence(0, 2).equals("0b")) {
            return 0;
        }

        int index = 2;
        char c = source.charAt(index);
        while (index <= source.length() && "01".contains("" + c)) {
            index += 1;
            c = source.charAt(index);
        }
        return index > 2 ? index : 0;
    }

    public static int matchHexNumber(CharSequence source) {
        if (source.length() < 3 || !source.subSequence(0, 2).equals("0x")) {
            return 0;
        }

        int index = 2;
        char c = source.charAt(index);
        while (index <= source.length() && "0123456789abcdefABCDEF".contains("" + c)) {
            index += 1;
            c = source.charAt(index);
        }
        return index > 2 ? index : 0;
    }

    public static int matchNumber(CharSequence source) {
        if (source.isEmpty()) {
            return 0;
        }

        char c = source.charAt(0);
        // Either 0 or a prefix e.g. 0x, 0b
        if (c == '0') {
            // Match 0
            if (source.length() == 1) {
                return 1;
            }
            c = source.charAt(1);
            // Match 0b
            if (c == 'b') {
                return matchBinNumber(source);
            }
            // Match 0x
            else if (c == 'x') {
                System.out.println("HEX");
                return matchHexNumber(source);
            }
            else {
                return 1;
            }
        }

        // Match integer
        return buildSpan(d -> "0123456789".contains("" + d)).apply(source);
    }
}
