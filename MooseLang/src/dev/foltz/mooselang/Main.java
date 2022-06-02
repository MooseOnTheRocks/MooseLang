package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;
import dev.foltz.mooselang.typing.types.*;

import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.parser.parsers.StatementParsers.parseProgram;
import static java.util.Map.entry;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();

        // -- Program source
        String program = """
            let a = 10
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
        System.out.println("== Parse Result");
        System.out.println(parseResult.getMsg());
        System.out.println(parseResult.state);
        System.out.println();
        List<ASTStmt> stmts = parseResult.get();

        System.out.println("== Program AST");
        stmts.stream().map(ASTPrinter::print).forEach(System.out::println);
        System.out.println();


        // -- Type Checking
//        System.out.println("== Type Checker");
//        Map<String, Type> builtinTypes = Map.ofEntries(
//            entry("Any", AnyType.INSTANCE),
//            entry("Int", TypeInt.INSTANCE),
//            entry("Bool", TypeBool.INSTANCE),
//            entry("None", TypeNone.INSTANCE),
//            entry("String", TypeString.INSTANCE)
//        );
//
//        Map<String, Type> globals = Map.ofEntries(
//            entry("+", new TypeFunc(List.of(TypeInt.INSTANCE, TypeInt.INSTANCE), TypeInt.INSTANCE)),
//            entry("&&", new TypeFunc(List.of(TypeBool.INSTANCE, TypeBool.INSTANCE), TypeBool.INSTANCE)),
//            entry("==", new TypeFunc(List.of(AnyType.INSTANCE, AnyType.INSTANCE), TypeBool.INSTANCE))
//        );
//
//        TypeChecker typeChecker = TypeChecker.withTypesAndGlobals(builtinTypes, globals);
//        for (ASTStmt stmt : stmts) {
//            if (stmt instanceof ASTStmtLet stmtLet) {
//                Type t = typeChecker.typeCheck(stmtLet);
//                System.out.println(ASTPrinter.print(stmt));
//                System.out.println("    :: " + t);
//                System.out.println();
//            }
//            else if (stmt instanceof ASTStmtFuncDef stmtFuncDef) {
//                Type t = typeChecker.typeCheck(stmtFuncDef);
//                System.out.println(ASTPrinter.print(stmt));
//                System.out.println("    :: " + t);
//                System.out.println();
//            }
//            else if (stmt instanceof ASTStmtTypeDef stmtTypeDef) {
//                Type t = typeChecker.typeCheck(stmtTypeDef);
//                System.out.println(ASTPrinter.print(stmt));
//                System.out.println("    :: " + t);
//                System.out.println();
//            }
//            else {
//                System.out.println("Cannot type check: " + stmt);
//                break;
//            }
//        }


        // -- Interpreter

        // [Builtin Type Names]
        Map<String, Type> builtinTypes = Map.ofEntries(
            entry("Any", AnyType.INSTANCE),
            entry("Int", TypeInt.INSTANCE),
            entry("Bool", TypeBool.INSTANCE),
            entry("None", TypeNone.INSTANCE),
            entry("String", TypeString.INSTANCE)
        );

        // [Builtin Definitions (Typed)]
//        Map<String, Type> globals = Map.ofEntries(
//            entry("+", new TypeFunc(List.of(TypeInt.INSTANCE, TypeInt.INSTANCE), TypeInt.INSTANCE)),
//            entry("&&", new TypeFunc(List.of(TypeBool.INSTANCE, TypeBool.INSTANCE), TypeBool.INSTANCE)),
//            entry("==", new TypeFunc(List.of(AnyType.INSTANCE, AnyType.INSTANCE), TypeBool.INSTANCE))
//        );


//
//        ASTEvaluator evaluator = new ASTEvaluator();
//        for (ASTStmt stmt : stmts) {
//            RTValue rtval = evaluator.evalAST(stmt);
//            System.out.println("Result: " + rtval);
//        }
    }
}
