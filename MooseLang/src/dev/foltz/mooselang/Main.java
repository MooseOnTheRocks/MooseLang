package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTPrinter;
import dev.foltz.mooselang.ast.statement.ASTStmt;
import dev.foltz.mooselang.ast.statement.ASTStmtFuncDef;
import dev.foltz.mooselang.ast.statement.ASTStmtLet;
import dev.foltz.mooselang.ast.statement.ASTStmtTypeDef;
import dev.foltz.mooselang.parser.ParseState;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;
import dev.foltz.mooselang.tokenizer.Tokenizer;
import dev.foltz.mooselang.typing.TypeChecker;
import dev.foltz.mooselang.typing.types.*;

import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.parser.parsers.Parsers.parseProgram;
import static java.util.Map.entry;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        System.out.println();

        // -- Program source
        String program = """
            type Person = { fname: String, lname: String }
            def Person(fname': String, lname': String) = new { fname = fname', lname = lname'}
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
        Map<String, Type> builtinTypes = Map.ofEntries(
            entry("Any", AnyType.INSTANCE),
            entry("Int", TypeInt.INSTANCE),
            entry("Bool", TypeBool.INSTANCE),
            entry("None", TypeNone.INSTANCE)
        );

        Map<String, Type> globals = Map.ofEntries(
            entry("+", new TypeFunc(List.of(TypeInt.INSTANCE, TypeInt.INSTANCE), TypeInt.INSTANCE)),
            entry("&&", new TypeFunc(List.of(TypeBool.INSTANCE, TypeBool.INSTANCE), TypeBool.INSTANCE)),
            entry("==", new TypeFunc(List.of(AnyType.INSTANCE, AnyType.INSTANCE), TypeBool.INSTANCE))
        );

        TypeChecker typeChecker = new TypeChecker(builtinTypes, globals);
        for (ASTStmt stmt : stmts) {
            if (stmt instanceof ASTStmtLet stmtLet) {
                Type t = typeChecker.typeCheck(stmtLet);
                System.out.println(ASTPrinter.print(stmt));
                System.out.println("    :: " + t);
                System.out.println();
            }
            else if (stmt instanceof ASTStmtFuncDef stmtFuncDef) {
                Type t = typeChecker.typeCheck(stmtFuncDef);
                System.out.println(ASTPrinter.print(stmt));
                System.out.println("    :: " + t);
                System.out.println();
            }
            else if (stmt instanceof ASTStmtTypeDef stmtTypeDef) {
                Type t = typeChecker.typeCheck(stmtTypeDef);
                System.out.println(ASTPrinter.print(stmt));
                System.out.println("    :: " + t);
                System.out.println();
            }
            else {
                System.out.println("Cannot type check: " + stmt);
                break;
            }
        }
    }
}
