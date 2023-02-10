package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.io.SourceDesc;

import java.util.List;

import static dev.foltz.mooselang.parser.ParserCombinators.*;

public class Parsers {
    public static final List<String> BAD_NAMES = List.of();
    public static final List<String> BAD_SYMBOLICS = List.of("_", "__");

    public static Parser<String> match(String p) {
        return s -> s.rem().startsWith(p) ? s.success(s.index + p.length(), p) : s.error();
    }

    public static <T> ParserState<T> parse(Parser<T> parser, SourceDesc source) {
        return parser.run(ParserState.success(source, 0, null));
    }

    public static final Parser<String> newlines = Parsers::newlines;
    public static final Parser<String> letter = Parsers::letter;
    public static final Parser<String> symbol = Parsers::symbol;
    public static final Parser<String> digit = Parsers::digit;
    public static final Parser<Double> number = Parsers::number;
    public static final Parser<String> name = Parsers::name;
    public static final Parser<String> symbolic = many1(symbol).map(ls -> String.join("", ls)).mapState(ss -> BAD_SYMBOLICS.contains(ss.result) ? ss.error() : ss);
    public static final Parser<String> string = Parsers::string;
    public static final Parser<String> comment = Parsers::comment;

    public static final Parser<String> ws =
        defaulted("",
            many1(
                any(Parsers.match(" "), Parsers.match("\t"))
                .map(s -> (String) s))
            .map(ls -> String.join("", ls)));

    public static final Parser<String> anyws =
            defaulted("",
                many1(
                    any(newlines, ws, comment)
                    .map(s -> (String) s))
                .map(ls -> String.join("", ls)));

    private static ParserState<String> newlines(ParserState<?> s) {
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

    private static ParserState<String> letter(ParserState<?> s) {
        final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : letters.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match letter.");
    }

    private static ParserState<String> symbol(ParserState<?> s) {
        final String symbols = "~!@#$%^&*-+./?|<>;:";
        for (char c : symbols.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match symbol.");
    }

    private static ParserState<String> digit(ParserState<?> s) {
        final String digits = "0123456789";
        for (char c : digits.toCharArray()) {
            if (s.rem().startsWith("" + c)) {
                return s.success(s.index + 1, "" + c);
            }
        }
        return s.error("Failed to match digit.");
    }

    private static ParserState<Double> number(ParserState<?> s) {
        var parseWhole = many1(digit).map(ls -> String.join("", ls));
        var parseFrac =
            all(match("."),
                defaulted("",
                    many1(digit)
                    .map(ls -> String.join("", ls))))
            .map(ls -> (String) ls.get(0) + ls.get(1));

        var parseNum = any(
                parseFrac,
                all(parseWhole, parseFrac).map(ls -> (String) ls.get(0) + ls.get(1)),
                parseWhole)
            .map(x -> (String) x);

        var res = parseNum.run(s);
        if (res.isError) {
            return res.error();
        }

        try {
            double value = Double.parseDouble(res.result);
            return res.success(res.index, value);
        }
        catch (NumberFormatException e) {
            return res.error();
        }
    }

    private static ParserState<String> name(ParserState<?> s) {
        var validFirst = any(match("_"), letter);
        var validRest = any(match("_"), letter, digit);
        return all(validFirst, many(validRest))
            .map(ls -> {
                var first = (String) ls.get(0);
                var rest = (List<String>) ls.get(1);
                return first + String.join("", rest);
            })
            .mapState(ss -> BAD_NAMES.contains(ss.result) ? ss.error() : ss)
            .run(s);
    }

    private static ParserState<String> string(ParserState<?> s) {
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
        return nextState;
    }

    private static ParserState<String> comment(ParserState<?> s) {
        if (s.isError) {
            return s.error();
        }

        var nextState = all(ws, match("--")).map(ls -> (String) ls.get(1)).run(s);
        if (nextState.isError) {
            return s.error();
        }

        while (true) {
            if (nextState.rem().isEmpty()) {
                return nextState;
            }
            var resNl = newlines.run(nextState);
            if (!resNl.isError) {
                return nextState.success(resNl.index, nextState.result);
            }
            nextState = nextState.success(nextState.index + 1, nextState.result + nextState.rem().charAt(0));
        }
    }
}
