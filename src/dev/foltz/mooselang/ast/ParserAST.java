package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.stmt.StmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.StmtLet;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.Parsers.*;
import static dev.foltz.mooselang.parser.ParserCombinators.*;

public class ParserAST {
    public static final Parser<ASTExpr> expr = ParserAST::expr;
    public static final Parser<ASTExpr> exprSimple = ParserAST::exprSimple;

    public static final Parser<ASTExpr> exprParen =
        all(
            Parsers.match("("),
            anyws,
            expr,
            anyws,
            Parsers.match(")"))
        .map(ls -> new ExprParen((ASTExpr) ls.get(2)));

    public static final List<String> KEYWORDS = List.of(
        "let", "in", "case", "of", "do", "->"
    );

    public static final Parser<ExprName> exprName =
//        all(letter, many(any(letter, digit)))
//        .map(ls -> {
//            List<String> arr = new ArrayList<>();
//            arr.add((String) ls.get(0));
//            arr.addAll((List<String>) ls.get(1));
//            return List.copyOf(arr);
//        })
//        .map(ls -> String.join("", ls))
        name
        .mapState(s -> KEYWORDS.stream().anyMatch(s.result::equals)
                ? s.error("Invalid name, clash with keyword")
                : s)
        .map(ExprName::new);

    public static final Parser<ExprString> exprString = Parsers.string.map(ExprString::new);

    public static final Parser<ExprSymbolic> exprSymbolic =
        symbolic
        .mapState(s -> KEYWORDS.stream().anyMatch(s.result::startsWith)
            ? s.error("Invalid name, clash with keyword") : s)
        .map(ExprSymbolic::new);

    public static final Parser<ExprNumber> exprNumber = Parsers.number.map(ExprNumber::new);

    public static final Parser<ExprDirective> exprDirective =
        all(Parsers.match("#"), exprName, anyws, exprSimple)
        .map(ls -> new ExprDirective(
            (ExprName) ls.get(1),
            (ASTExpr) ls.get(3)
        ));

    public static final Parser<ExprLetIn> exprLetIn =
        all(
            Parsers.match("let"),
            anyws,
            exprName,
            anyws,
            Parsers.match("="),
            anyws,
            expr,
            anyws,
            Parsers.match("in"),
            anyws,
            expr)
        .map(ls -> new ExprLetIn(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6),
            (ASTExpr) ls.get(10)));

    public static final Parser<ExprTuple> exprTuple =
        all(match("("),
            anyws,
            joining(all(anyws, match(","), anyws), expr),
            anyws,
            match(")"))
        .map(ls -> (List<ASTExpr>) ls.get(2))
        .map(ExprTuple::new);

    public static final Parser<ExprCaseOfBranch> exprCaseOfBranch =
        intersperse(anyws,
            expr,
            match("->"),
            expr)
        .map(ls -> new ExprCaseOfBranch(
            (ASTExpr) ls.get(0),
            (ASTExpr) ls.get(2)
        ));

    public static final Parser<ExprCaseOf> exprCaseOf =
        intersperse(anyws,
            match("case"),
            expr,
            match("of"),
            joining(anyws,
                any(exprCaseOfBranch,
                    intersperse(anyws, match("("), exprCaseOfBranch, match(")"))
                    .map(ls -> ls.get(1)))))
        .map(ls -> new ExprCaseOf(
            (ASTExpr) ls.get(1),
            (List<ExprCaseOfBranch>) ls.get(3)));

    public static final Parser<ExprLambda> exprLambda =
        all(
            Parsers.match("\\"),
            ws,
            exprName,
            ws,
            Parsers.match(":"),
            ws,
            exprName,
            anyws,
            Parsers.match("->"),
            anyws,
            expr)
        .map(ls -> new ExprLambda(
            ((ExprName) ls.get(2)).name,
            ((ExprName) ls.get(6)).name,
            (ASTExpr) ls.get(10)));

    public static final Parser<StmtLet> stmtLet =
        all(
            Parsers.match("let"),
            anyws,
            exprName,
            anyws,
            Parsers.match("="),
            anyws,
            expr)
        .map(ls -> new StmtLet(
            (ExprName) ls.get(2),
            (ASTExpr) ls.get(6)));

    public static final Parser<StmtDef> stmtDef =
        intersperse(anyws,
            exprName,
            joining(anyws,
                intersperse(anyws, exprName, match(":"), exprName))
            .map(ls -> {
                var names = new ArrayList<String>();
                var types = new ArrayList<String>();
                for (List<?> l : ls) {
                    names.add(((ExprName) l.get(0)).name);
                    types.add(((ExprName) l.get(2)).name);
                }
                return List.of(names, types);
            }),
            match("="),
            expr)
        .map(ls -> new StmtDef(
            (ExprName) ls.get(0),
            (List<String>) ((List<?>) ls.get(1)).get(0),
            (List<String>) ((List<?>) ls.get(1)).get(1),
            (ASTExpr) ls.get(3)
        ));
//
//        all(
//            exprName,
//            many(
//                all(ws, exprSimple)
//                .map(ls -> (ASTExpr) ls.get(1))),
//            anyws,
//            Parsers.match("="),
//            anyws,
//            expr)
//        .map(ls -> new StmtDef(
//            (ExprName) ls.get(0),
//            (List<ASTExpr>) ls.get(1),
//            (ASTExpr) ls.get(5)));

    // -- Function definitions

    private static ParserState<ASTExpr> exprSimple(ParserState<?> s) {
        return all(
            optional(ws),
            any(
                exprLetIn,
                exprCaseOf,
                exprParen,
                exprTuple,
                exprLambda,
                exprName,
                exprSymbolic,
                exprNumber,
                exprString))
            .map(ls -> (ASTExpr) ls.get(1))
        .run(s);
    }

    private static int getOpPrec(ASTExpr op) {
        if (op instanceof ExprDirective directive) {
            return 100;
        }
        else if (op instanceof ExprSymbolic sym) {
            return switch (sym.symbol) {
                case ";" -> 0;
                case "+", "-" -> 25;
                case "*", "/" -> 50;
                case "^" -> 75;
                default -> 100;
            };
        }
        else {
            return -1;
        }
    }

    private static ParserState<ASTExpr> expr(ParserState<?> s) {
        var expr = exprSimple(s);
        if (expr.isError) {
            return expr;
        }
        return expr_inner(expr, 0);
    }

    private static ParserState<ASTExpr> expr_inner(ParserState<ASTExpr> s, int minPrec) {
        if (s.isError) {
            return s;
        }

        var lhs = s;
        var lookahead = exprSimple(lhs);
        while (!lookahead.isError) {
//            System.out.println("Lookahead: " + lookahead.result);
            if (lookahead.result instanceof ExprSymbolic && getOpPrec(lookahead.result) >= minPrec) {
                var op = lookahead;
                var rhs = exprSimple(op);
                lookahead = exprSimple(rhs);
//                System.out.println("lhs = " + lhs.result);
//                System.out.println("op = " + op.result);
//                System.out.println("rhs = " + rhs.result);
//                System.out.println("lookahead = " + lookahead.result);
                while (!lookahead.isError) {
//                    System.out.println("Inner lookahead: " + lookahead.result);
                    if (lookahead.result instanceof ExprSymbolic && getOpPrec(lookahead.result) > getOpPrec(op.result)) {
                        if (getOpPrec(lookahead.result) > getOpPrec(op.result)) {
                            rhs = expr_inner(rhs, getOpPrec(op.result) + 1);
                        }
                        else {
                            rhs = expr_inner(rhs, getOpPrec(op.result));
                        }
                        lookahead = exprSimple(rhs);
                    }
                    else if (!(lookahead.result instanceof ExprSymbolic)) {
//                        System.out.println("SPECIAL BREAKING");
//                        System.out.println("lhs = " + lhs.result);
//                        System.out.println("op = " + op.result);
//                        System.out.println("rhs = " + rhs.result);
//                        System.out.println("lookahead = " + lookahead.result);
                        rhs = lookahead.success(lookahead.index, new ExprApply(rhs.result, lookahead.result));
                        lookahead = exprSimple(rhs);
                    }
                    else {
//                        System.out.println("BREAKING");
//                        System.out.println("lhs = " + lhs.result);
//                        System.out.println("op = " + op.result);
//                        System.out.println("rhs = " + rhs.result);
//                        System.out.println("lookahead = " + lookahead.result);
                        break;
                    }
                }

                if (rhs.result == null) {
                    System.out.println("Got a null: " + op.result + ", " + lhs.result);
                    return rhs.error();
//                    break;
//                    lhs = rhs.success(rhs.index, new ExprApply(op.result, lhs.result));
                }
                else {
//                    System.out.println("SUCCESSFUL BIN-OP PARSE: " + op.result);
                    var symbol = ((ExprSymbolic) op.result).symbol;
                    if (symbol.equals(";")) {
                        lhs = rhs.success(rhs.index, new ExprChain(lhs.result, rhs.result));
                    }
                    else {
                        lhs = rhs.success(rhs.index, new ExprApply(new ExprApply(op.result, lhs.result), rhs.result));
                    }
                }
            }
            else if (lookahead.result instanceof ExprSymbolic) {
                break;
            }
            else if (lhs.result instanceof ExprSymbolic) {
                return lhs.error();
            }
            else {
//                System.out.println("OUTER BREAKING");
//                System.out.println("LHS: " + lhs.result);
//                System.out.println("LOOKAHEAD: " + lookahead.result);
                var rhs = lookahead;
                lhs = rhs.success(rhs.index, new ExprApply(lhs.result, rhs.result));
//                System.out.println("NEW LHS: " + lhs.result);
                lookahead = exprSimple(lhs);
//                var rhs = lookahead;
//                lhs = rhs.success(rhs.index, new ExprApply(lhs.result, rhs.result));
//                System.out.println("NEW LHS: " + lhs.result);
//                lookahead = exprSimple(lhs);
//                break;
            }
        }
//        System.out.println("RETURNING LHS: " + lhs.result);
        return lhs;

        /*
        var lhs = s;
        var lookahead = exprSimple(lhs);
        while (!lookahead.isError) {
            if (lookahead.result instanceof ExprSymbolic symbolic && getOpPrec(lookahead.result) >= minPrec) {
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
        */
    }
}
