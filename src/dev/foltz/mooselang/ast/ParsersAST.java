package dev.foltz.mooselang.ast;

import dev.foltz.mooselang.ast.nodes.expr.*;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtDef;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmtLet;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.Parser;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.Parsers.*;
import static dev.foltz.mooselang.parser.ParserCombinators.*;

public class ParsersAST {
    public static final List<String> KEYWORDS = List.of(
            "let", "in", "case", "of", "do", "->"
    );
}
