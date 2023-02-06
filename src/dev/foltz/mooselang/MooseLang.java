package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.typing.Scope;
import dev.foltz.mooselang.typing.TypedAST;
import dev.foltz.mooselang.typing.comp.Lambda;
import dev.foltz.mooselang.typing.comp.Producer;
import dev.foltz.mooselang.typing.value.NumberType;
import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.Thunk;
import dev.foltz.mooselang.typing.value.Unit;
import dev.foltz.mooselang.parser.Combinators;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.SourceDesc;

import static dev.foltz.mooselang.parser.Parsers.*;

public class MooseLang {
    public static void main(String[] args) {
//        var source = SourceDesc.fromString("test", "let axe = 200");
        var source = SourceDesc.fromFile("tests", "test.msl");

        var toplevel = Combinators.any(
            anyws,
            comment,
            expr,
            stmtLet,
            stmtDef
        );

        var parser = Combinators.many(toplevel);

        var res = Parsers.parse(parser, source);

        boolean failed = res.isError || !res.rem().isEmpty();

        if (failed) {
            System.err.println("== Error");
            System.err.println("From source: " + res.source.name());
            System.err.println("At: " + res.index);
            if (res.isError) {
                System.err.println("Parsing failed...");
                System.out.println(res.error);
            }
            else {
                System.err.println("Did not consume all input...");
            }
            System.err.println("-- Remaining input:");
            System.err.println(res.rem());
            System.err.println();
        }
        else {
            System.out.println("== Success");
            System.out.println("From source: " + res.source.name());

//            System.out.println("-- AST Nodes:");
            var asts = res.result.stream().filter(e -> e instanceof ASTNode).map(e -> (ASTNode) e).toList();
//            asts.forEach(System.out::println);

            var globalScope = new Scope(null);
            var builtinPrint =
                new Thunk(
                    new Lambda("_print_1", new StringType(),
                        new Producer(new Unit())));
            globalScope = globalScope.put("print", builtinPrint);
            var builtinAdd =
                new Thunk(
                    new Lambda("_add_1", new NumberType(),
                        new Lambda("_add_2", new NumberType(),
                            new Producer(new NumberType()))));
            globalScope = globalScope.put("+",  builtinAdd);

            var globalASTEval = new TypedAST(globalScope, null);

            System.out.println("-- Types");
            for (ASTNode ast : asts) {
                System.out.println("Original AST:");
                System.out.println(ast);
                System.out.println("Type:");

                var typed = globalASTEval.evalTypeAST(ast);
                System.out.println(typed.result);
            }
        }
    }
}
