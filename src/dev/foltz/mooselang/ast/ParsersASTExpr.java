package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;
import dev.foltz.mooselang.parser.Parsers;

import java.util.List;

import static dev.foltz.mooselang.parser.ParserCombinators.*;
import static dev.foltz.mooselang.parser.ParserCombinators.all;
import static dev.foltz.mooselang.ast.ParsersASTType.type;
import static dev.foltz.mooselang.parser.Parsers.*;
import static dev.foltz.mooselang.parser.Parsers.anyws;

public class ParsersASTExpr {
    public static final Parser<ASTExpr> expr = ParsersASTExpr::expr;
    public static final Parser<ASTExpr> exprSimple = ParsersASTExpr::exprSimple;
    public static final Parser<ASTExprParen> exprParen =
        intersperse(anyws, match("("), expr, match(")"))
        .map(ls -> new ASTExprParen((ASTExpr) ls.get(1)));

    public static final Parser<ASTExprName> exprName =
        name.mapState(s -> ParsersAST.KEYWORDS.stream().anyMatch(s.result::equals)
            ? s.error("Invalid name, clash with keyword") : s)
        .map(ASTExprName::new);

    public static final Parser<ASTExprString> exprString = Parsers.string.map(ASTExprString::new);

    public static final Parser<ASTExprSymbolic> exprSymbolic =
        symbolic.mapState(s -> ParsersAST.KEYWORDS.stream().anyMatch(s.result::startsWith)
            ? s.error("Invalid name, clash with keyword") : s)
        .map(ASTExprSymbolic::new);

    public static final Parser<ASTExprNumber> exprNumber = Parsers.number.map(ASTExprNumber::new);

    public static final Parser<ASTExprLetIn> exprLetIn =
        intersperse(anyws,
            Parsers.match("let"),
            exprName,
            Parsers.match("="),
            expr,
            Parsers.match("in"),
            expr)
        .map(ls -> new ASTExprLetIn(
            (ASTExprName) ls.get(1),
            (ASTExpr) ls.get(3),
            (ASTExpr) ls.get(5)));

    public static final Parser<ASTExprTuple> exprTuple =
        intersperse(anyws,
            match("("),
            joining(all(anyws, match(","), anyws), expr),
            match(")"))
        .map(ls -> new ASTExprTuple((List<ASTExpr>) ls.get(1)));

    public static final Parser<ASTExprCaseOfBranch> exprCaseOfBranch =
        intersperse(anyws,
            expr,
            match("->"),
            expr)
        .map(ls -> new ASTExprCaseOfBranch(
            (ASTExpr) ls.get(0),
            (ASTExpr) ls.get(2)));

    public static final Parser<ASTExprCaseOf> exprCaseOf =
        intersperse(anyws,
            match("case"),
            expr,
            match("of"),
            joining(anyws,
                any(exprCaseOfBranch,
                    intersperse(anyws, match("("), exprCaseOfBranch, match(")"))
                    .map(ls -> ls.get(1)))))
        .map(ls -> new ASTExprCaseOf(
            (ASTExpr) ls.get(1),
            (List<ASTExprCaseOfBranch>) ls.get(3)));

    public static final Parser<ASTExprLambda> exprLambda =
        intersperse(anyws,
            Parsers.match("\\"),
            exprName,
            Parsers.match(":"),
            type,
            Parsers.match("->"),
            expr)
        .map(ls -> new ASTExprLambda(
            ((ASTExprName) ls.get(1)).name,
            ((ASTType) ls.get(3)),
            (ASTExpr) ls.get(5)));

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
        if (op instanceof ASTExprSymbolic sym) {
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
        var res = expr_inner(expr, 0);
        if (res.isError) {
            return res;
        }
        else {
            var annotation = intersperse(anyws, match(":"), type).map(ls -> (ASTType) ls.get(1));
            var ares = annotation.run(res);
            if (ares.isError) {
                return res;
            }
            return ares.success(ares.index, new ASTExprTypeAnnotated(res.result, ares.result));
        }
    }

    private static ParserState<ASTExpr> expr_inner(ParserState<ASTExpr> s, int minPrec) {
        if (s.isError) {
            return s;
        }

        var lhs = s;
        var lookahead = exprSimple(lhs);
        while (!lookahead.isError) {
//            System.out.println("Lookahead: " + lookahead.result);
            if (lookahead.result instanceof ASTExprSymbolic && getOpPrec(lookahead.result) >= minPrec) {
                var op = lookahead;
                var rhs = exprSimple(op);
                lookahead = exprSimple(rhs);
//                System.out.println("lhs = " + lhs.result);
//                System.out.println("op = " + op.result);
//                System.out.println("rhs = " + rhs.result);
//                System.out.println("lookahead = " + lookahead.result);
                while (!lookahead.isError) {
//                    System.out.println("Inner lookahead: " + lookahead.result);
                    if (lookahead.result instanceof ASTExprSymbolic && getOpPrec(lookahead.result) > getOpPrec(op.result)) {
                        if (getOpPrec(lookahead.result) > getOpPrec(op.result)) {
                            rhs = expr_inner(rhs, getOpPrec(op.result) + 1);
                        }
                        else {
                            rhs = expr_inner(rhs, getOpPrec(op.result));
                        }
                        lookahead = exprSimple(rhs);
                    }
                    else if (!(lookahead.result instanceof ASTExprSymbolic)) {
//                        System.out.println("SPECIAL BREAKING");
//                        System.out.println("lhs = " + lhs.result);
//                        System.out.println("op = " + op.result);
//                        System.out.println("rhs = " + rhs.result);
//                        System.out.println("lookahead = " + lookahead.result);
                        rhs = lookahead.success(lookahead.index, new ASTExprApply(rhs.result, lookahead.result));
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
                    var symbol = ((ASTExprSymbolic) op.result).symbol;
                    if (symbol.equals(";")) {
                        lhs = rhs.success(rhs.index, new ASTExprChain(lhs.result, rhs.result));
                    }
                    else {
                        lhs = rhs.success(rhs.index, new ASTExprApply(new ASTExprApply(op.result, lhs.result), rhs.result));
                    }
                }
            }
            else if (lookahead.result instanceof ASTExprSymbolic) {
                break;
            }
            else if (lhs.result instanceof ASTExprSymbolic) {
                return lhs.error();
            }
            else {
//                System.out.println("OUTER BREAKING");
//                System.out.println("LHS: " + lhs.result);
//                System.out.println("LOOKAHEAD: " + lookahead.result);
                var rhs = lookahead;
                lhs = rhs.success(rhs.index, new ASTExprApply(lhs.result, rhs.result));
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
    }
}
