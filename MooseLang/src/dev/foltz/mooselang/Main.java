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
                let greeting1 = "Hello"
                let greeting2 = "World"
                print(greeting1, greeting2)
                let numbers = [1, 2, 3, 4, 5]
                print(numbers)
                """;
        String program2 = """
                let greetingOuter = "Hello"
                {
                    let greetingInner = "World"
                    print(greetingOuter)
                    print(greetingInner)
                }
                print(greetingOuter)
                """;
        String program3 = """
                def userFunc(x) = {
                    print("Begin userFunc")
                    print(x)
                    print("End userFunc")
                }
                
                userFunc("Hello, userFunc!")
                """;
        String program4 = """
                let x = 10
                def userFunc() = print(x)
                userFunc()
                """;
        String program5 = """
                def func(0) = "Zero"
                def func(1) = "One"
                def func(2) = "Two"
                def func(n) = "Some number"
                
                let results = [func(0), func(1), func(2), func(3)]
                print(results)
                
                """;

        String program = program5;

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
