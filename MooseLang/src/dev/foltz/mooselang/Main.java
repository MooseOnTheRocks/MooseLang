package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.expression.ASTExpr;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.Parsers.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();


        // -- Program source
        String program = """
                let ls = ["Hello", 42, 200, "again"]
                let a = 20
                "another"
                """;

        System.out.println("== Program Source");
        System.out.println(program);
        System.out.println();


        // -- Tokens
        List<Token> tokens = Tokenizer.tokenize(program)
                .filter(t -> t.type != TokenType.T_WHITESPACE)
                .filter(t -> t.type != TokenType.T_NEWLINE)
                .filter(t -> t.type != TokenType.T_COMMENT)
                .toList();

        System.out.println("== Program Tokens");
        System.out.println(tokens);
        System.out.println();


        // -- AST
        ParseState initial = new ParseState(List.of(), tokens);
        var programParser = many(parseStmt).map(objs -> objs.stream().map(obj -> (ASTStmt) obj).toList());
        var parseResult = programParser.parse(initial);
        System.out.println(parseResult.get());
        List<ASTStmt> stmts = parseResult.get();
        System.out.println("== Program AST");
        stmts.stream()
                .map(ASTPrinter::print)
                .forEach(System.out::println);
        System.out.println();


        // -- Interpretation
//        FlatScope globalScope = new FlatScope();
//        globalScope.bind("print", new RTBuiltinPrint());
//
//        ASTEvaluator evaluator = new ASTEvaluator(globalScope);
//
//        System.out.println("== Interpreter");
//        for (ASTStmt stmt : stmts) {
//            RTObject result = stmt.accept(evaluator);
////            System.out.println(result);
//        }
    }
}
