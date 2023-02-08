package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ir.*;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.parser.BasicParsers;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.Scope;
import dev.foltz.mooselang.typing.TypedAST;
import dev.foltz.mooselang.typing.comp.Lambda;
import dev.foltz.mooselang.typing.comp.Producer;
import dev.foltz.mooselang.typing.value.NumberType;
import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.Thunk;
import dev.foltz.mooselang.typing.value.Unit;
import dev.foltz.mooselang.parser.ParserCombinators;
import dev.foltz.mooselang.parser.SourceDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.parser.Parsers.*;

public class MooseLang {
    public static void main(String[] args) {
//        var source = SourceDesc.fromString("test", "let axe = 200");
        var source = SourceDesc.fromFile("tests", "test.msl");

        var toplevel = ParserCombinators.any(
            anyws,
            comment,
            expr
//            stmtLet,
//            stmtDef
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

            var builtinNum2Str =
                new Thunk(
                    new Lambda("_num2str_1", new NumberType(),
                        new Producer(new StringType())));
            globalScope = globalScope.put("num2str", builtinNum2Str);

            var globalASTEval = new TypedAST(globalScope, null);

            System.out.println("-- Types");
            for (ASTNode ast : asts) {
                System.out.println("Original AST:");
                System.out.println(ast);
                System.out.println("Type:");

                var typed = globalASTEval.evalTypeAST(ast);
                System.out.println(typed.result);
            }
            System.out.println();

            System.out.println("-- Compile");
            var topLevelIR = new ArrayList<IRNode>();
            for (ASTNode ast : asts) {
                System.out.println("Original AST:");
                System.out.println(ast);
                System.out.println("Compiled:");

                var compiled = new ParserIR().compile(ast);
                System.out.println(compiled);
                topLevelIR.add(compiled);
            }
            System.out.println();

            System.out.println("-- Interpreter");
            for (IRNode node : topLevelIR) {
                if (!(node instanceof IRComp comp)) {
                    throw new RuntimeException("Expected computation, cannot interpret: " + node);
                }
                Map<String, IRValue> globalScopeIR = Map.of(
                    "print", IRBuiltin.PRINT_THUNK,
                    "+", IRBuiltin.ADD_THUNK,
                    "num2str", IRBuiltin.NUM2STR_THUNK
                );
                var initialState = new Interpreter(comp, List.of(), new dev.foltz.mooselang.rt.Scope(null, null, globalScopeIR),false);
                var state = initialState;
                System.out.println("Initial state:");
                System.out.println(state);
                while (!state.terminated) {
                    state = state.stepExecution();
                }
                System.out.println("-- Interpreter Terminated");
                System.out.println("IR:");
                System.out.println(node);
                System.out.println("Result:");
                System.out.println(state);
            }
        }
    }
}
