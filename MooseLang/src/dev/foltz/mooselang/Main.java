package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();


        // -- Program source
        String program = Programs.program21;

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
        List<ASTStmt> stmts = Parser.parse(tokens).toList();

        System.out.println("== Program AST");
        stmts.stream()
                .map(ASTPrinter::asString)
                .forEach(System.out::println);
        System.out.println();


        // -- Interpretation

    }
}
