package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.interpreter.ASTEvaluator;
import dev.foltz.mooselang.interpreter.FlatScope;
import dev.foltz.mooselang.interpreter.rt.RTBuiltinPrint;
import dev.foltz.mooselang.interpreter.rt.RTObject;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();


        // -- Program source
        String program = """
                let a = 100
                
                def foo(x) {
                    let c = 20
                    print(x, c)
                }
                
                foo()
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
        List<ASTStmt> stmts = Parser.parse(tokens).toList();

        System.out.println("== Program AST");
        stmts.stream()
                .map(ASTPrinter::print)
                .forEach(System.out::println);
        System.out.println();


        // -- Interpretation
        FlatScope globalScope = new FlatScope();
        globalScope.bind("print", new RTBuiltinPrint());

        ASTEvaluator evaluator = new ASTEvaluator(globalScope);

        System.out.println("== Interpreter");
        for (ASTStmt stmt : stmts) {
            RTObject result = stmt.accept(evaluator);
//            System.out.println(result);
        }
    }
}
