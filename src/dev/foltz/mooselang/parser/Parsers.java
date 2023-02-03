package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
    public static final Parser<String> symbol = Parsers::symbol;
    public static final Parser<String> digit = Parsers::digit;
    public static final Parser<Double> number = Parsers::number;
    public static final Parser<String> comment = Parsers::comment;

    // -- AST Parsers
    public static final Parser<ASTExpr> expr = Parsers::expr;
    public static final Parser<ASTExpr> exprSimple = Parsers::exprSimple;

    public static final Parser<ASTExpr> exprParen =
        all(
            match("("),
            anyws,
            expr,
            anyws,
            match(")"))
        .map(ls -> new ExprParen((ASTExpr) ls.get(2)));

    public static final List<String> KEYWORDS = List.of(
        "let", "in"
    );

    public static final Parser<ExprName> exprName =
        all(letter, many(any(letter, digit)))
        .map(ls -> {
            List<String> arr = new ArrayList<>();
            arr.add((String) ls.get(0));
            arr.addAll((List<String>) ls.get(1));
            return List.copyOf(arr);
        })
        .map(ls -> String.join("", ls))
        .mapState(s -> KEYWORDS.stream().anyMatch(s.result::startsWith)
                ? s.error("Invalid name, clash with keyword")
                : s)
        .map(ExprName::new);

    public static final Parser<ExprString> exprString = Parsers::string;

    public static final Parser<ExprSymbolic> exprSymbolic =
        many1(symbol)
        .map(ls -> String.join("", ls))
        .map(ExprSymbolic::new);

    public static final Parser<ExprNumber> exprNumber = number.map(ExprNumber::new);

    public static final Parser<ExprLetIn> exprLetIn =
        all(
            match("let"),
            anyws,
            exprName,
            anyws,
            match("="),
            anyws,
            expr,
            anyws,
            match("in"),
            anyws,
            expr)
        .map(ls -> new ExprLetIn(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6),
            (ASTExpr) ls.get(10)));

    public static final Parser<ExprLambda> exprLambda =
        all(
            match("\\"),
            ws,
            exprName,
            ws,
            match(":"),
            ws,
            exprName,
            anyws,
            match("->"),
            anyws,
            expr)
        .map(ls -> new ExprLambda(
            ((ExprName) ls.get(2)).name,
            ((ExprName) ls.get(6)).name,
            (ASTExpr) ls.get(10)));

    public static final Parser<StmtLet> stmtLet =
        all(
            match("let"),
            anyws,
            exprName,
            anyws,
            match("="),
            anyws,
            expr)
        .map(ls -> new StmtLet(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6)));

    public static final Parser<StmtDef> stmtDef =
        all(
            exprName,
            many(
                all(ws, exprSimple)
                .map(ls -> (ASTExpr) ls.get(1))),
            anyws,
            match("="),
            anyws,
            expr)
        .map(ls -> new StmtDef(
            (ExprName) ls.get(0),
            (List<ASTExpr>) ls.get(1),
            (ASTExpr) ls.get(5)));

    // -- Function definitions

    public static ParserState<ASTExpr> exprSimple(ParserState<?> s) {
        return all(
            optional(ws),
            any(
                exprLetIn,
                exprLambda,
                exprParen,
                exprName,
                exprSymbolic,
                exprNumber,
                exprString))
            .map(ls -> (ASTExpr) ls.get(1))
        .run(s);
    }

    public static int getOpPrec(ASTExpr op) {
        if (op instanceof ExprSymbolic sym) {
            return switch (sym.symbol) {
                case ";" -> 0;
                case "+", "-" -> 25;
                case "*", "/" -> 50;
                case "^" -> 75;
                default -> 10;
            };
        }
        else {
            return -1;
        }
    }

    public static ParserState<ASTExpr> expr(ParserState<?> s) {
        var expr = exprSimple(s);
        if (expr.isError) {
            return expr;
        }
        return expr_inner(expr, 0);
    }

    public static ParserState<ASTExpr> expr_inner(ParserState<ASTExpr> s, int minPrec) {
        if (s.isError) {
            return s;
        }

        var lhs = s;
        var lookahead = exprSimple(lhs);
        while (!lookahead.isError) {
            if (lookahead.result instanceof ExprSymbolic symbolic && symbolic.symbol.equals(";")) {
                var op = lookahead;
                var rhs = all(anyws, expr).map(ls -> (ASTExpr) ls.get(1)).run(op);
                if (rhs.isError) {
                    return lhs;
                }
                return rhs.success(rhs.index, new ExprChain(lhs.result, rhs.result));
            }
            else if (lookahead.result instanceof ExprSymbolic symbolic && getOpPrec(lookahead.result) >= minPrec) {
                var op = lookahead;
                var rhs = exprSimple(op);
                if (rhs.isError) {
                    return lhs;
                }
                lookahead = exprSimple(rhs);

                while (!lookahead.isError) {
                    rhs = lookahead.result instanceof ExprSymbolic && getOpPrec(lookahead.result) > getOpPrec(op.result)
                        // Operator
                        ? expr_inner(rhs, getOpPrec(op.result) + 1)
                        // Function application
                        : lookahead.success(lookahead.index, new ExprApply(rhs.result, lookahead.result));
                    lookahead = exprSimple(rhs);
                }
                lhs = rhs.success(rhs.index, new ExprApply(new ExprApply(op.result, lhs.result), rhs.result));
            }
            // Function application
            else {
                lhs = lookahead.success(lookahead.index, new ExprApply(lhs.result, lookahead.result));
                lookahead = exprSimple(lhs);
            }
        }
        return lhs;
    }

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
