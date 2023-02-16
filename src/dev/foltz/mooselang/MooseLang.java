package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ir.*;
import dev.foltz.mooselang.ir.nodes.IRGlobalDef;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.io.SourceDesc;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.rt.InterpreterOld;
import dev.foltz.mooselang.rt.ScopeOld;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.ir.PrettyPrintIR.prettyPrint;
import static dev.foltz.mooselang.parser.Parsers.anyws;
import static dev.foltz.mooselang.parser.Parsers.comment;
import static dev.foltz.mooselang.parser.ParserCombinators.any;
import static dev.foltz.mooselang.parser.ParserCombinators.many;
import static dev.foltz.mooselang.ast.ParserAST.*;

public class MooseLang {
    public static final TypedIR localTyper = new TypedIR(Map.of());
    public static final TypedIR globalTyper = new TypedIR(Map.of(
            "print", (TypeValue) localTyper.typeOf(Builtins.PRINT),
            "+", (TypeValue) localTyper.typeOf(Builtins.ADD),
            "-", (TypeValue) localTyper.typeOf(Builtins.SUBTRACT),
            "*", (TypeValue) localTyper.typeOf(Builtins.MULTIPLY),
            "num2str", (TypeValue) localTyper.typeOf(Builtins.NUM2STR)
    ));

    public static final ScopeOld globalScope = new ScopeOld(null, null, Map.of(
            "print", Builtins.PRINT,
            "+", Builtins.ADD,
            "-", Builtins.SUBTRACT,
            "*", Builtins.MULTIPLY,
            "num2str", Builtins.NUM2STR
    ));

//    public static final Function<IRComp, Interpreter> globalInterpreter = term -> new Interpreter(term, List.of(), globalScope, false);

    public static void testParseIR() {
        var sourceIR = SourceDesc.fromFile("irout", "test.mslir");
        var topLevelIR = any(anyws, comment, ParserIR.irComp);
        var parserIR = many(topLevelIR);
        var res = Parsers.parse(parserIR, sourceIR);
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

            System.out.println("-- IR Nodes:");
            var irs = res.result.stream().filter(e -> e instanceof IRNode).map(e -> (IRNode) e).toList();
            irs.forEach(System.out::println);
        }
    }

    public static void testInterp() {
        var sourceAST = SourceDesc.fromFile("tests", "test.msl");
        var topLevelAST = any(
            anyws,
            comment,
            expr
        );
        var parserAST = many(topLevelAST);
        var res = Parsers.parse(parserAST, sourceAST);
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
            for (ASTNode ast : asts) {
                System.out.println("Original AST:");
                System.out.println(ast);
                System.out.println("Compiled:");

                var compiled = new CompilerIR().compileNode(ast);
                System.out.println(compiled);
                topLevelIR.add(compiled);
                System.out.println(prettyPrint(compiled));
                System.out.println("Type:");
                var typed = globalTyper.typeOf(compiled);
                System.out.println(typed);
                System.out.println("Execution:");
                if (compiled instanceof IRComp comp) {
                    var state = new InterpreterOld(comp, List.of(), globalScope, false);
                    while (!state.terminated) {
                        state = state.stepExecution();
                    }
                    System.out.println("Finished:");
                    System.out.println("Term: " + state.term);
                    System.out.println("Scope: " + state.scope.allBindings(Map.of()));
                    System.out.println("Stack: " + state.stack);
                    System.out.println();
                    System.out.println("Name: " + sourceAST.name());
//                    var sep = Objects.equals(File.separator, "\\") ? "\\\\" : File.separator;
//                    var sourceSep = sourceAST.name().split(sep);
//                    var sourceName = sourceSep[sourceSep.length - 1];
//                    sourceName += sourceName.endsWith("msl") ? "ir" : ".mslir";
//                    Path savePath = Path.of("irout", sourceName).toAbsolutePath();
//                    System.out.println("Saving to: " + savePath);
//                    SourceDesc compiledCode = SourceDesc.fromString(sourceName, prettyPrint(compiled));
//                    SourceDesc.saveAsFile(compiledCode, savePath.toString());
                }
                else {
                    System.out.println("Value: " + compiled);
                }
            }
            System.out.println();
        }
    }

    public static List<ASTNode> parseASTs(SourceDesc sourceCode) {
        var parseTopLevelAST = any(anyws, comment, stmtDef, expr);
        var parserAST = many(parseTopLevelAST);
        var res = Parsers.parse(parserAST, sourceCode);
        boolean failed = res.isError || !res.rem().isEmpty();
        if (!failed) {
            return res.result.stream().filter(x -> x instanceof ASTNode).map(x -> (ASTNode) x).toList();
        }

        System.err.println("== AST Parse Error");
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

        throw new RuntimeException("Oops");
    }

    public static IRNode compileIR(ASTNode ast) {
        return CompilerIR.compile(ast);
    }

    public static List<IRComp> parseIR(SourceDesc sourceIR) {
        var parseTopLevelIR = any(anyws, comment, ParserIR.irComp);
        var parserIR = many(parseTopLevelIR);
        var res = Parsers.parse(parserIR, sourceIR);
        boolean failed = res.isError || !res.rem().isEmpty();
        if (!failed) {
            return res.result.stream().filter(x -> x instanceof IRComp).map(x -> (IRComp) x).toList();
        }

        System.err.println("== IR Parse Error");
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

        throw new RuntimeException("Oops");
    }

    public static void main(String[] args) {
        Map<String, IRValue> builtins = Map.of(
            "+", Builtins.ADD,
            "-", Builtins.SUBTRACT,
            "*", Builtins.MULTIPLY,
            "print", Builtins.PRINT,
            "num2str", Builtins.NUM2STR
        );

        var source = SourceDesc.fromFile("tests", "test.msl");
        var asts = parseASTs(source);
        System.out.println("== AST");
        asts.forEach(System.out::println);
        System.out.println();

        var irs = asts.stream().map(MooseLang::compileIR).toList();
        var topLevelDefs = irs.stream().filter(node -> node instanceof IRGlobalDef).map(n -> (IRGlobalDef) n).toList();
        var topLevelComps = irs.stream().filter(node -> node instanceof IRComp).map(n -> (IRComp) n).toList();
        var irModule = new IRModule(topLevelDefs, topLevelComps);
        System.out.println("== IR Module");
        System.out.println(PrettyPrintIR.prettyPrint(irModule));
        System.out.println();

        Map<String, IRValue> globalDefs = new HashMap<>(builtins);
        for (var topDef : irModule.topLevelDefs) {
            var name = topDef.name;
            if (globalDefs.containsKey(name)) {
                throw new RuntimeException("Illegal redefinition of " + name);
            }
            globalDefs.put(name, topDef.value);
        }
        globalDefs = Map.copyOf(globalDefs);

//        for (var globalDef : globalDefs.entrySet()) {
//            var name = globalDef.getKey();
//            var value = globalDef.getValue();
//            System.out.println("-- " + name);
//            System.out.println(value);
//            System.out.println();
//        }

        var globalInterp = new Interpreter(null, globalDefs, List.of(), false);
        int comps = 0;
        for (var topComp : irModule.topLevelComps) {
            System.out.println("== Starting computation #" + comps);
            var res = globalInterp.withTerm(topComp).stepAll();
            System.out.println("== Finished computation #" + comps);
            System.out.println(res.term);
            System.out.println("---");
            comps += 1;
        }

//        if (ir instanceof IRComp term) {
//            var interp = new Interpreter(term, builtins, List.of(), false);
//            interp = interp.stepAll();
//            System.out.println("== Execution finished");
//            System.out.println("Term: " + interp.term);
//            System.out.println("Context: " + interp.context);
//            System.out.println("Stack: " + interp.stack);
//        }
//        else {
//            System.out.println("Not computation: " + ir);
//        }
//        testInterp();
//        testParseIR();
//        var sourceFile = SourceDesc.fromFile("tests", "test.msl");
//        var astNodes = parseASTs(sourceFile);
//        var irNodes = astNodes.stream().map(MooseLang::compileIR).toList();
//        System.out.println("-- IR");
//        for (IRNode irNode : irNodes) {
//            System.out.println(prettyPrint(irNode));
//        }
    }
}
