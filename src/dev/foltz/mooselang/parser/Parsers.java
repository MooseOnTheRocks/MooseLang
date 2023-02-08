package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.stmt.StmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.StmtLet;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.ParserCombinators.*;

public class Parsers {
    // -- Basic text parsers
    public static final Parser<String> nl = BasicParsers::newlines;
    public static final Parser<String> ws =
        defaulted("",
            many1(
                any(BasicParsers.match(" "), BasicParsers.match("\t"))
                .map(s -> (String) s))
            .map(ls -> String.join("", ls)));

    public static final Parser<String> comment = BasicParsers::comment;

    public static final Parser<String> wsnl =
        many1(
            any(nl, ws, comment)
            .map(s -> (String) s))
        .map(ls -> String.join("", ls));

    public static final Parser<String> anyws = defaulted("", wsnl);
    public static final Parser<String> letter = BasicParsers::letter;
    public static final Parser<String> symbol = BasicParsers::symbol;
    public static final Parser<String> digit = BasicParsers::digit;
    public static final Parser<Double> number = BasicParsers::number;

    // -- AST Parsers
    public static final Parser<ASTExpr> expr = Parsers::expr;
    public static final Parser<ASTExpr> exprSimple = Parsers::exprSimple;

    public static final Parser<ASTExpr> exprParen =
        all(
            BasicParsers.match("("),
            anyws,
            expr,
            anyws,
            BasicParsers.match(")"))
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

    public static final Parser<ExprString> exprString = BasicParsers::string;

    public static final Parser<ExprSymbolic> exprSymbolic =
        many1(symbol)
        .map(ls -> String.join("", ls))
        .map(ExprSymbolic::new);

    public static final Parser<ExprNumber> exprNumber = number.map(ExprNumber::new);

    public static final Parser<ExprDirective> exprDirective =
        all(BasicParsers.match("#"), exprName, anyws, exprSimple)
        .map(ls -> new ExprDirective(
            (ExprName) ls.get(1),
            (ASTExpr) ls.get(3)
        ));

    public static final Parser<ExprLetIn> exprLetIn =
        all(
            BasicParsers.match("let"),
            anyws,
            exprName,
            anyws,
            BasicParsers.match("="),
            anyws,
            expr,
            anyws,
            BasicParsers.match("in"),
            anyws,
            expr)
        .map(ls -> new ExprLetIn(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6),
            (ASTExpr) ls.get(10)));

    public static final Parser<ExprLambda> exprLambda =
        all(
            BasicParsers.match("\\"),
            ws,
            exprName,
            ws,
            BasicParsers.match(":"),
            ws,
            exprName,
            anyws,
            BasicParsers.match("->"),
            anyws,
            expr)
        .map(ls -> new ExprLambda(
            ((ExprName) ls.get(2)).name,
            ((ExprName) ls.get(6)).name,
            (ASTExpr) ls.get(10)));

    public static final Parser<StmtLet> stmtLet =
        all(
            BasicParsers.match("let"),
            anyws,
            exprName,
            anyws,
            BasicParsers.match("="),
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
            BasicParsers.match("="),
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
                exprDirective,
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
        if (op instanceof ExprDirective directive) {
            return 100;
        }
        else if (op instanceof ExprSymbolic sym) {
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
}
