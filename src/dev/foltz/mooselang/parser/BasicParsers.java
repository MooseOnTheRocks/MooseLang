package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.nodes.expr.ExprString;
import dev.foltz.mooselang.source.SourceDesc;

import static dev.foltz.mooselang.parser.ParserCombinators.*;

public class BasicParsers {
    public static ParserState<String> newlines(ParserState<?> s) {
        var nl = any(match("\n"), match("\r\n")).map(ss -> (String) ss);
        var matchedState = nl.run(s);
        if (matchedState.isError) {
            return matchedState.error();
        }
        String total = matchedState.result;
        var ws = any(match(" "), match("\t"));
        var potentialState = matchedState;
        while (true) {
            var matchWs = ws.run(potentialState);
            if (!matchWs.isError) {
                potentialState = matchWs.success(matchWs.index, total + matchWs.result);
                continue;
            }
            var matchNl = nl.run(potentialState);
            if (!matchNl.isError) {
                potentialState = matchNl.success(matchNl.index, total + matchNl.result);
                matchedState = potentialState;
                continue;
            }
            break;
        }
        return matchedState;
    }

    public static ParserState<String> letter(ParserState<?> s) {
        final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : letters.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match letter.");
    }

    public static ParserState<String> symbol(ParserState<?> s) {
        final String symbols = "~!@#$%^&*-+./?|<>;:";
        for (char c : symbols.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match symbol.");
    }

    public static ParserState<String> digit(ParserState<?> s) {
        final String digits = "0123456789";
        for (char c : digits.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match digit.");
    }

    public static ParserState<Double> number(ParserState<?> s) {
        var res = many1(Parsers.digit).map(ls -> String.join("", ls)).run(s);
        if (res.isError) {
            return res.error();
        }

        String digits = res.result;
        try {
            double value = Double.parseDouble(digits);
            return res.success(res.index, value);
        }
        catch (NumberFormatException e) {
            return res.error();
        }
    }

    public static ParserState<ExprString> string(ParserState<?> s) {
        if (s.isError) {
            return s.error();
        }

        var firstQuote = match("\"").run(s);
        if (firstQuote.isError) {
            return firstQuote.error();
        }

        var nextState = firstQuote.success(firstQuote.index, "");
        while (true) {
            if (nextState.rem().isEmpty()) {
                return s.error("Failed to parse string");
            } else if (nextState.rem().startsWith("\"")) {
                nextState = nextState.success(nextState.index + 1, nextState.result);
                break;
            }
            nextState = nextState.success(nextState.index + 1, nextState.result + nextState.rem().charAt(0));
        }
        return nextState.success(nextState.index, new ExprString(nextState.result));
    }

    public static ParserState<String> comment(ParserState<?> s) {
        if (s.isError) {
            return s.error();
        }

        var nextState = all(Parsers.ws, match("--")).map(ls -> (String) ls.get(1)).run(s);
        if (nextState.isError) {
            return s.error();
        }

        while (true) {
            if (nextState.rem().isEmpty()) {
                return nextState;
            }
            var resNl = Parsers.nl.run(nextState);
            if (!resNl.isError) {
                return nextState.success(resNl.index, nextState.result);
            }
            nextState = nextState.success(nextState.index + 1, nextState.result + nextState.rem().charAt(0));
        }
    }

    public static Parser<String> match(String p) {
        return s -> s.rem().startsWith(p) ? s.success(s.index + p.length(), p) : s.error();
    }

    public static <T> ParserState<T> parse(Parser<T> parser, SourceDesc source) {
        return parser.run(ParserState.success(source, 0, null));
    }
}
