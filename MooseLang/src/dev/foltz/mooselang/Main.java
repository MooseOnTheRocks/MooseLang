package dev.foltz.mooselang;

//import dev.foltz.mooselang.parse.Parser;
import dev.foltz.mooselang.interpreter.Interpreter;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        String program = """
                numbers = [1, 2, 3, 4, 5]
                print(numbers)
                """;
        System.out.println("== Program");
        System.out.println(program);
        System.out.println();

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.feed(program);
        List<Token> tokens = new ArrayList<>();
        while (!tokenizer.isEmpty()) {
            Token token = tokenizer.nextToken();
            if (token.type == TokenType.T_WHITESPACE || token.type == TokenType.T_NEWLINE) {
                continue;
            }
            tokens.add(token);
        }
        System.out.println("== Tokens");
        System.out.println(tokens);
        System.out.println();


        Parser parser = new Parser();
        tokens.forEach(parser::feed);
        List<ASTStmt> stmts = parser.parse();
        System.out.println("== AST");
        stmts.forEach(System.out::println);
        System.out.println();


        Interpreter interp = new Interpreter();
        stmts.forEach(interp::feed);
        System.out.println("== Interpreter");
        while (!interp.isEmpty()) {
            interp.execStmt();
        }
    }
}
