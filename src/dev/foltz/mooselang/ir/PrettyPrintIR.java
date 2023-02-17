package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRGlobalDef;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.value.ValueNumber;
import dev.foltz.mooselang.typing.value.ValueString;
import dev.foltz.mooselang.typing.value.ValueUnit;

import java.util.stream.Collectors;

public class PrettyPrintIR extends VisitorIR<String> {
    public final int indentLevel;
    public final String indentString;
    public final boolean isInline;

    public PrettyPrintIR(int indentLevel, String indentString, boolean isInline) {
        this.indentLevel = indentLevel;
        this.indentString = indentString;
        this.isInline = isInline;
    }

    public PrettyPrintIR indent() {
        return new PrettyPrintIR(indentLevel + 1, indentString, isInline);
    }

    public PrettyPrintIR dedent() {
        return new PrettyPrintIR(indentLevel - 1, indentString, isInline);
    }

    public PrettyPrintIR inline() {
        return new PrettyPrintIR(indentLevel, indentString, true);
    }

    public String getIndent() {
        return isInline ? "" : indentString.repeat(indentLevel);
    }

    public String getNextIndent() {
        return isInline ? "" : getIndent() + indentString;
    }

    public static String prettyPrint(IRNode node) {
        return node.apply(new PrettyPrintIR(0, "    ", false));
    }

    public String pprint(IRNode node) {
        return node.apply(this);
    }

    public String pprint(String string) {
        return string;
    }

    @Override
    public String visit(IRModule module) {
        var sb = new StringBuilder();
        sb.append("\n-- Top Level Definitions\n\n");
        for (var topDef : module.topLevelDefs) {
            sb.append(pprint(topDef));
            sb.append("\n");
            sb.append("\n");
        }
        sb.append("\n-- Top Level Computations\n\n");
        for (var topComp : module.topLevelComps) {
            sb.append(pprint(topComp));
            sb.append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visit(IRGlobalDef globalDef) {
        return "def " + globalDef.name + " = " + indent().pprint(globalDef.value);
    }

    @Override
    public String visit(IRCompLet bind) {
        return "let " + inline().pprint(bind.name) + " = " + pprint(bind.value) + " in\n" +
            getIndent() + pprint(bind.body);
    }

    @Override
    public String visit(IRCompDo bind) {
        return "do (\n" +
            getNextIndent() + indent().pprint(bind.boundComp) + "\n" +
            getIndent() + ") = " + inline().pprint(bind.name) + "\n" +
            getIndent() + "in\n" +
            getIndent() + pprint(bind.body);
    }

    @Override
    public String visit(IRCompProduce produce) {
        return "#produce " + pprint(produce.value);
    }

    @Override
    public String visit(IRCompLambda lambda) {
        var lambdaType = lambda.paramType;
        var typeName = "";
        if (lambdaType.equals(new ValueNumber())) typeName = "Number";
        else if (lambdaType.equals(new ValueString())) typeName = "String";
        else if (lambdaType.equals(new ValueUnit())) typeName = "Unit";
        else throw new RuntimeException("Unknown type for lambda param: " + lambdaType);

        return "(\\" + lambda.paramName + ": " + typeName + " ->\n" +
                getNextIndent() + indent().pprint(lambda.body) + "\n" +
                getIndent() + ")";
    }

    @Override
    public String visit(IRCompCaseOf caseOf) {
        return "case " + inline().pprint(caseOf.value) + " of (\n" +
            caseOf.branches.stream().map(b -> getNextIndent() + indent().pprint(b)).collect(Collectors.joining("\n")) + "\n" +
            getIndent() + ")";
    }

    @Override
    public String visit(IRCompCaseOfBranch caseOfBranch) {
        return inline().pprint(caseOfBranch.pattern) + " -> " + indent().pprint(caseOfBranch.body);
    }

    @Override
    public String visit(IRValueThunk thunk) {
        return "#thunk " + pprint(thunk.comp);
    }

    @Override
    public String visit(IRCompPush push) {
        return "#push " + pprint(push.value) + "\n" +
            getIndent() + pprint(push.then);
    }

    @Override
    public String visit(IRCompForce force) {
        return "#force " + pprint(force.thunk);
    }

    @Override
    public String visit(IRValueTuple tuple) {
        return "(" + tuple.values.stream().map(inline()::pprint).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public String visit(IRValueName name) {
        return name.name;
    }

    @Override
    public String visit(IRValueString string) {
        return "\"" + string.value + "\"";
    }

    @Override
    public String visit(IRValueNumber number) {
        return "" + number.value;
    }
}
