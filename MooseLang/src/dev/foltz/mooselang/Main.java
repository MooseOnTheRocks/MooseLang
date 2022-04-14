package dev.foltz.mooselang;

import dev.foltz.mooselang.interpreter.Interpreter;
import dev.foltz.mooselang.interpreter.runtime.RTFuncPrint;
import dev.foltz.mooselang.interpreter.runtime.RTNone;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTPrinter;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        String program1 = """
                greeting1 = "Hello"
                greeting2 = "World"
                print(greeting1, greeting2)
                numbers = [1, 2, 3, 4, 5]
                print(numbers)
                """;
        String program2 = """
                greetingOuter = "Hello"
                {
                    greetingInner = "World"
                    print(greetingOuter)
                    print(greetingInner)
                }
                print(greetingOuter)
                """;
        System.out.println("== Program");
        System.out.println(program1);
        System.out.println();

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.feed(program1);
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
        ASTPrinter printer = new ASTPrinter();
        stmts.forEach(stmt -> stmt.accept(printer));
        System.out.println(printer);
        System.out.println();


        System.out.println("== Interpreter");
        Interpreter interpreter = new Interpreter(Map.of("print", new RTFuncPrint()));
        stmts.forEach(interpreter::feed);
        while (!interpreter.isEmpty()) {
            RTObject res = interpreter.execNext();
            if (res instanceof RTNone) {
                continue;
            }
            System.out.println("==> " + res);
        }
    }
}
