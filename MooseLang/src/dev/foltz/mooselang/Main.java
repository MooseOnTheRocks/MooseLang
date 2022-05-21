package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;
import dev.foltz.mooselang.typing.ASTTypeChecker;
import dev.foltz.mooselang.typing.type.TypeUnion;
import dev.foltz.mooselang.typing.type.builtin.BuiltinTypes;
import dev.foltz.mooselang.typing.type.literal.TypeLiteralInt;

import java.util.List;

import static dev.foltz.mooselang.parser.Parsers.parseProgram;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();

        // -- Program source
        String program = """
            let a: Bool = True
            let b: Int = 0x42
            let c: None = None
            let d: String = "Hello, World!"
            let divResult: Int | None = None
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
        var parseResult = parseProgram.parse(initial);
        System.out.println(parseResult.getMsg());
        System.out.println(parseResult.state);
        List<ASTStmt> stmts = parseResult.get();

        System.out.println("== Program AST");
        stmts.stream().map(ASTPrinter::print).forEach(System.out::println);
        System.out.println();


        // -- Type Checking
        ASTTypeChecker typeChecker = new ASTTypeChecker();
        typeChecker.bindType("None", BuiltinTypes.TYPE_NONE);
        typeChecker.bindType("Bool", BuiltinTypes.TYPE_BOOL);
        typeChecker.bindType("Int", BuiltinTypes.TYPE_INT);
        typeChecker.bindType("String", BuiltinTypes.TYPE_STRING);
        typeChecker.bindType("Binary", new TypeUnion(List.of(new TypeLiteralInt(0), new TypeLiteralInt(1))));

        System.out.println("== Type Checking");
        for (ASTStmt stmt : stmts) {
            var typeCheck = typeChecker.evalType(stmt);
            System.out.println(typeCheck);
        }
        System.out.println();
    }
}
