package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmt;
import dev.foltz.mooselang.io.SourceDesc;
import dev.foltz.mooselang.ir.Builtins;
import dev.foltz.mooselang.ir.CompilerIR;
import dev.foltz.mooselang.ir.PrettyPrintIR;
import dev.foltz.mooselang.ir.nodes.IRDefType;
import dev.foltz.mooselang.ir.nodes.IRDefValue;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.comp.IRCompProduce;
import dev.foltz.mooselang.ir.nodes.type.IRTypeSum;
import dev.foltz.mooselang.ir.nodes.type.tag.IRTypeTagName;
import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.ir.nodes.value.IRValueFunctionHandle;
import dev.foltz.mooselang.ir.nodes.value.IRValueTagged;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.rt.Interpreter;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.ast.ParsersASTExpr.expr;
import static dev.foltz.mooselang.ast.ParsersASTStmt.stmtDef;
import static dev.foltz.mooselang.ast.ParsersASTStmt.stmtDefSumType;
import static dev.foltz.mooselang.parser.ParserCombinators.any;
import static dev.foltz.mooselang.parser.ParserCombinators.many1;
import static dev.foltz.mooselang.parser.Parsers.anyws;
import static dev.foltz.mooselang.parser.Parsers.comment;

public class MooseLang {
    public static final Map<String, IRValue> BUILTINS = Map.of(
        "+", new IRValueFunctionHandle("+", List.of(
            new IRDefValue("+", Builtins.ADD),
            new IRDefValue("+", Builtins.CONCAT))),
        "-", Builtins.SUBTRACT,
        "*", Builtins.MULTIPLY,
        "print", Builtins.PRINT,
        "num2str", Builtins.NUM2STR
    );

//    public static final Map<String, TypeValue> BUILTINS_TYPED = BUILTINS.keySet().stream().collect(Collectors.toMap(name -> name, name -> (TypeValue) new TypedIR(Map.of()).typeOf(BUILTINS.get(name))));

    public static void main(String[] args) {
        var source = SourceDesc.fromFile("tests", "test.msl");
        System.out.println("== Input: " + source.name());
        var asts = parseAST(source);
        System.out.println("== Parsed ASTs successfully.");
        var ir = compileIR(asts);
        System.out.println("== Compiled IR successfully.");
        var savePath = new String[] {"irout", "test.mslir"};
        var prettyIr = PrettyPrintIR.prettyPrint(ir);
        SourceDesc.saveAsFile(SourceDesc.fromString("ir", prettyIr), "irout", "test.mslir");
        System.out.println("== Saved IR to file: " + Paths.get("irout", "test.mslir").toAbsolutePath());
        System.out.println("== Evaluating...");
        System.out.println();
        var eval = evaluate(ir);
        System.out.println(eval.term);
//        var pret = eval.find("pret");
//        System.out.println("type of pret: " + new TypedIR(BUILTINS_TYPED).typeOf(pret));
//        System.out.println(BUILTINS_TYPED);
    }

    public static List<ASTNode> parseAST(SourceDesc source) {
        var parser =
            many1(any(anyws, comment, stmtDef, stmtDefSumType, expr))
            .map(ls -> ls.stream().filter(n -> n instanceof ASTExpr || n instanceof ASTStmt).toList())
            .map(ls -> (List<ASTNode>) ls);
        var asts = Parsers.parse(parser, source);
        if (asts.isError || !asts.rem().isEmpty()) {
            throw new RuntimeException("Failed to parse source, remaining:\n" + asts.rem());
        }
        return asts.result;
    }

    public static IRModule compileIR(List<ASTNode> asts) {
        var irs = asts.stream().map(CompilerIR::compile).toList();
        var topLevelTypeDefs = irs.stream().filter(ir -> ir instanceof IRDefType).map(ir -> (IRDefType) ir).toList();
        var topLevelDefs = irs.stream().filter(ir -> ir instanceof IRDefValue).map(ir -> (IRDefValue) ir).toList();
        var topLevelComps = irs.stream().filter(ir -> ir instanceof IRComp || ir instanceof IRValue).map(ir -> ir instanceof IRValue value ? new IRCompProduce(value) : (IRComp) ir).toList();
        // Convert functions with same name into multiple-dispatch function
        // I.e. all the functions become one functions which takes a type parameter as the first argument
        // (can be curried to take multiple type parameters, like values).
//        var uniqueDefs = topLevelDefs.stream().map(def -> def.name).collect(Collectors.toSet());
        var defMapping = new HashMap<String, List<IRDefValue>>();
        for (var def : topLevelDefs) {
            var name = def.name;
            if (!defMapping.containsKey(name)) {
                defMapping.put(name, new ArrayList<>());
            }
            defMapping.get(name).add(def);
        }

        var typeDefMappings = new HashMap<String, List<IRDefType>>();
        for (var typeDef : topLevelTypeDefs) {
            var name = typeDef.name;
            if (!typeDefMappings.containsKey(name)) {
                typeDefMappings.put(name, new ArrayList<>());
            }
            typeDefMappings.get(name).add(typeDef);
        }

        return new IRModule(typeDefMappings, defMapping, topLevelComps);
    }

    public static Interpreter evaluate(IRModule module) {
        var context = new HashMap<>(BUILTINS);
        for (var topDefs : module.topLevelDefs.entrySet()) {
            var name = topDefs.getKey();
            var defs = topDefs.getValue();
            context.put(name, new IRValueFunctionHandle(name, defs));
        }

        for (var topTypeDef : module.topLevelTypeDefs.entrySet()) {
            var name = topTypeDef.getKey();
            var defs = topTypeDef.getValue();
            for (var def : defs) {
                if (def.type instanceof IRTypeSum sumType) {
                    for (int i = 0; i < sumType.conNames.size(); i++) {
                        var conName = sumType.conNames.get(i);
                        var conParams = sumType.params.get(i);
                        if (conParams.size() > 0) {
                            throw new RuntimeException("Product-type definitions unsupported.");
                        }
                        context.put(conName, new IRValueTagged(new IRTypeTagName(conName), sumType));
                    }
                }
            }
        }

        var globalInterpreter = new Interpreter(null, context, List.of(), false, Map.of());
        Interpreter lastEval = null;
        for (var topComp : module.topLevelComps) {
            lastEval = globalInterpreter.withTerm(topComp).stepAll();
        }
        return lastEval;
    }
}
