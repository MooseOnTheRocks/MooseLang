package dev.foltz.mooselang;

import dev.foltz.mooselang.interpreter.Interpreter;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncCons;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncHead;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncPrint;
import dev.foltz.mooselang.interpreter.runtime.RTNone;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.interpreter.runtime.builtins.RTFuncTail;
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
        String program6 = """
                def print2(0, 0) = print("0", "0")
                def print2("a", "a") = print("a", "a")
                def print2(a, b) = print("Two objects:", a, b)
                
                print2(0, 0)
                print2("a", "a")
                print2(0, 1)
                print2("a", "b")
                print2(0, "a")
                """;
        String program7 = """
                def foreach(f, []) = None
                def foreach(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    f(h)
                    foreach(f, rs)
                }
                
                foreach(print, [1, 2, 3, 4])
                """;
        String program8 = """
                let ls = cons(1, cons(2, cons(3, cons(4, []))))
                print(ls)
                """;
        String program9 = """
                // Returns a list containing two of the given element.
                def double(e) = [e, e]
                
                // Returns a new list with the function f applied to each element of the list.
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                // Original list
                let myList = [1, 2, 3, 4]
                // Mapped list
                let mappedList = map(double, myList)
                print(myList)
                print(mappedList)
                """;
        String program10 = """
                def printAll(ls) =
                    for elem in ls do print(elem)
                
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                def reversed([]) = []
                def reversed(ls) = {
                    // Inline function definition
                    def reversed'(acc, []) = { acc }
                    def reversed'(acc, rem) = {
                        let h = head(rem)
                        let rs = tail(rem)
                        reversed'(cons(h, acc), rs)
                    }
                    
                    // Code blocks implicitly return the last expression.
                    reversed'([], ls)
                }
                
                let nums = [1, 2, 3, 4]
                let nums' = reversed(nums)
                print(nums)
                print(nums')
                """;
        String program11 = """
                def map(f, []) = []
                def map(f, ls) = {
                    let h = head(ls)
                    let rs = tail(ls)
                    cons(f(h), map(f, rs))
                }
                
                def reversed([]) = []
                def reversed(ls) = {
                    // Inline function definition
                    def reversed'(acc, []) = { acc }
                    def reversed'(acc, rem) = {
                        let h = head(rem)
                        let rs = tail(rem)
                        reversed'(cons(h, acc), rs)
                    }
                    
                    // Code blocks implicitly return the last expression.
                    reversed'([], ls)
                }
                
                def pair(a, b) = [a, b]
                
                // Lambda expressions (anonymous function definitions)
                let double = lambda x => pair(x, x)
                
                let nums = [1, 2, 3, 4]
                // Single quotes allowed in variable names
                let nums' = reversed(map(double, nums))
                print(nums')
                """;

        String program = program11;

        System.out.println("== Program");
        System.out.println(program);
        System.out.println();

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.feed(program);
        List<Token> tokens = new ArrayList<>();
        while (!tokenizer.isEmpty()) {
            Token token = tokenizer.nextToken();
            if (token.type == TokenType.T_WHITESPACE || token.type == TokenType.T_NEWLINE || token.type == TokenType.T_COMMENT) {
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
        Map<String, RTObject> globals = Map.of(
                "print", new RTFuncPrint(),
                "head", new RTFuncHead(),
                "tail", new RTFuncTail(),
                "cons", new RTFuncCons()
        );
        Interpreter interpreter = new Interpreter(globals);
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
