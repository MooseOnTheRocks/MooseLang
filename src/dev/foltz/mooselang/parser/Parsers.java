package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.ASTExpr;
import dev.foltz.mooselang.ast.ExprName;
import dev.foltz.mooselang.ast.ExprNumber;
import dev.foltz.mooselang.ast.StmtLet;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.Combinators.*;

public class Parsers {
    // -- Basic text parsers
    public static final Parser<String> nl = Parsers::newlines;
    public static final Parser<String> ws =
        defaulted("",
            many1(
                any(match(" "), match("\t"))
                .map(s -> (String) s))
            .map(ls -> String.join("", ls)));

    public static final Parser<String> wsnl =
        many1(
            any(nl, ws)
            .map(s -> (String) s))
        .map(ls -> String.join("", ls));

    public static final Parser<String> anyws = defaulted("", wsnl);
    public static final Parser<String> letter = Parsers::letter;
    public static final Parser<String> digit = Parsers::digit;
    public static final Parser<Double> number = Parsers::number;
    public static final Parser<String> comment = Parsers::comment;

    // -- AST Parsers
    public static final Parser<ExprName> exprName =
        all(letter, many(any(letter, digit)))
        .map(ls -> {
            List<String> arr = new ArrayList<>();
            arr.add((String) ls.get(0));
            arr.addAll((List<String>) ls.get(1));
            return List.copyOf(arr);
        })
        .map(ls -> String.join("", ls))
        .map(ExprName::new);

    public static final Parser<ExprNumber> exprNumber = number.map(ExprNumber::new);

    public static final Parser<StmtLet> stmtLet =
        all(match("let"), anyws, exprName, anyws, match("="), anyws, exprNumber)
        .map(ls -> new StmtLet(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6)));

    // -- Function definitions

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
        var res = many1(digit).map(ls -> String.join("", ls)).run(s);
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

    public static ParserState<String> comment(ParserState<?> s) {
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
            var resNl = nl.run(nextState);
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