package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.nodes.ASTNode;
import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.stmt.ASTStmt;
import dev.foltz.mooselang.io.SourceDesc;
import dev.foltz.mooselang.ir.Builtins;
import dev.foltz.mooselang.ir.CompilerIR;
import dev.foltz.mooselang.ir.PrettyPrintIR;
import dev.foltz.mooselang.ir.TypedIR;
import dev.foltz.mooselang.ir.nodes.IRGlobalDef;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.comp.IRProduce;
import dev.foltz.mooselang.ir.nodes.value.IRValue;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.foltz.mooselang.ast.ParserAST.expr;
import static dev.foltz.mooselang.ast.ParserAST.stmtDef;
import static dev.foltz.mooselang.parser.ParserCombinators.any;
import static dev.foltz.mooselang.parser.ParserCombinators.many1;
import static dev.foltz.mooselang.parser.Parsers.anyws;
import static dev.foltz.mooselang.parser.Parsers.comment;

public class MooseLang {
    public static final Map<String, IRValue> BUILTINS = Map.of(
        "+", Builtins.ADD,
        "-", Builtins.SUBTRACT,
        "*", Builtins.MULTIPLY,
        "print", Builtins.PRINT,
        "num2str", Builtins.NUM2STR
    );

    public static final Map<String, TypeValue> BUILTINS_TYPED = BUILTINS.keySet().stream().collect(Collectors.toMap(name -> name, name -> (TypeValue) new TypedIR(Map.of()).typeOf(BUILTINS.get(name))));

    public static void main(String[] args) {
        var source = SourceDesc.fromFile("tests", "test.msl");
        var asts = parseAST(source);
        System.out.println(asts);
        var ir = compileIR(asts);
        System.out.println(PrettyPrintIR.prettyPrint(ir));
        var eval = evaluate(ir);
        var pret = eval.find("pret");
        System.out.println("type of pret: " + new TypedIR(BUILTINS_TYPED).typeOf(pret));
        System.out.println(eval.term);
        System.out.println(BUILTINS_TYPED);
    }

    public static List<ASTNode> parseAST(SourceDesc source) {
        var parser =
            many1(any(anyws, comment, stmtDef, expr))
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
        var topLevelDefs = irs.stream().filter(ir -> ir instanceof IRGlobalDef).map(ir -> (IRGlobalDef) ir).toList();
        var topLevelComps = irs.stream().filter(ir -> ir instanceof IRComp || ir instanceof IRValue).map(ir -> ir instanceof IRValue value ? new IRProduce(value) : (IRComp) ir).toList();
        return new IRModule(topLevelDefs, topLevelComps);
    }

    public static Interpreter evaluate(IRModule module) {
        var context = new HashMap<>(BUILTINS);
        for (var topDef : module.topLevelDefs) {
            context.put(topDef.name, topDef.value);
        }

        var globalInterpreter = new Interpreter(null, context, List.of(), false);
        Interpreter lastEval = null;
        for (var topComp : module.topLevelComps) {
            lastEval = globalInterpreter.withTerm(topComp).stepAll();
        }
        return lastEval;
    }
}
