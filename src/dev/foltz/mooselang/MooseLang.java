package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ir.*;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.parser.BasicParsers;
import dev.foltz.mooselang.parser.ParserCombinators;
import dev.foltz.mooselang.parser.SourceDesc;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.rt.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static dev.foltz.mooselang.parser.Parsers.*;

public class MooseLang {
    public static void main(String[] args) {
//        var source = SourceDesc.fromString("test", "let axe = 200");
        var source = SourceDesc.fromFile("tests", "test.msl");

        var toplevel = ParserCombinators.any(
            anyws,
            comment,
            expr,
//            stmtLet,
            stmtDef
        );

        var parser = ParserCombinators.many(toplevel);

        var res = BasicParsers.parse(parser, source);

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

            System.out.println("-- AST Nodes:");
            var asts = res.result.stream().filter(e -> e instanceof ASTNode).map(e -> (ASTNode) e).toList();
            asts.forEach(System.out::println);

            System.out.println("-- Compile");
            var topLevelIR = new ArrayList<IRNode>();
            var globalTyper = new TypedIR(Map.of(
                "print", IRBuiltin.PRINT_TYPE,
                "+", IRBuiltin.ADD_TYPE,
                "num2str", IRBuiltin.NUM2STR_TYPE
            ));

            Scope globalScope = new Scope(null, null, Map.of(
                "print", IRBuiltin.PRINT_THUNK,
                "+", IRBuiltin.ADD_THUNK,
                "num2str", IRBuiltin.NUM2STR_THUNK
            ));
            Function<IRComp, Interpreter> globalInterpreter = term -> new Interpreter(term, List.of(), globalScope, false);
            for (ASTNode ast : asts) {
                System.out.println("Original AST:");
                System.out.println(ast);
                System.out.println("Compiled:");

                var compiled = new CompilerIR().compile(ast);
                System.out.println(compiled);
                topLevelIR.add(compiled);
                System.out.println(PrettyPrintIR.prettyPrint(compiled));
                System.out.println("Type:");
                var typed = globalTyper.typeOf(compiled);
                System.out.println(typed);
                System.out.println("Execution:");
                if (compiled instanceof IRComp comp) {
                    var state = globalInterpreter.apply(comp);
                    while (!state.terminated) {
                        state = state.stepExecution();
                    }
                    System.out.println("Finished: " + state);
                }
                else {
                    System.out.println("Value: " + compiled);
                }
            }
            System.out.println();
        }
    }
}
