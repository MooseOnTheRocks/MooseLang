package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ASTExprName;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtLet;
import dev.foltz.mooselang.ast.nodes.type.ASTType;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.Parsers;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.ast.ParsersASTExpr.expr;
import static dev.foltz.mooselang.ast.ParsersASTExpr.exprName;
import static dev.foltz.mooselang.parser.ParserCombinators.*;
import static dev.foltz.mooselang.parser.ParserCombinators.intersperse;
import static dev.foltz.mooselang.parser.Parsers.anyws;
import static dev.foltz.mooselang.parser.Parsers.match;

public class ParsersASTStmt {
    public static final Parser<ASTStmtLet> stmtLet =
        all(Parsers.match("let"),
            anyws,
            exprName,
            anyws,
            Parsers.match("="),
            anyws,
            expr)
        .map(ls -> new ASTStmtLet(
            (ASTExprName) ls.get(2),
            (ASTExpr) ls.get(6)));

    public static final Parser<ASTStmtDef> stmtDef =
        intersperse(anyws,
            exprName,
            joining(anyws,
                intersperse(anyws, exprName, match(":"), ParsersASTType.type))
                .map(ls -> {
                    var names = new ArrayList<String>();
                    var types = new ArrayList<ASTType>();
                    for (List<?> l : ls) {
                        names.add(((ASTExprName) l.get(0)).name);
                        types.add(((ASTType) l.get(2)));
                    }
                    return List.of(names, types);}),
            match("="),
            expr)
        .map(ls -> new ASTStmtDef(
            (ASTExprName) ls.get(0),
            (List<String>) ((List<?>) ls.get(1)).get(0),
            (List<ASTType>) ((List<?>) ls.get(1)).get(1),
            (ASTExpr) ls.get(3)));
}
