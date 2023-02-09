package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.IRName;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.ir.nodes.value.IRString;
import dev.foltz.mooselang.typing.value.NumberType;
import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.Unit;

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
    public String visit(IRLet bind) {
        return "let " + inline().pprint(bind.name) + " = " + pprint(bind.value) + " in\n" +
            getIndent() + pprint(bind.body);
    }

    @Override
    public String visit(IRDo bind) {
        return "do (\n" +
            getNextIndent() + indent().pprint(bind.boundComp) + "\n" +
            getIndent() + ") = " + inline().pprint(bind.name) + "\n" +
            getIndent() + "in\n" +
            getIndent() + pprint(bind.body);
    }

    @Override
    public String visit(IRProduce produce) {
        return "#produce " + pprint(produce.value);
    }

    @Override
    public String visit(IRLambda lambda) {
        var lambdaType = lambda.paramType;
        var typeName = "";
        if (lambdaType.equals(new NumberType())) typeName = "Number";
        else if (lambdaType.equals(new StringType())) typeName = "String";
        else if (lambdaType.equals(new Unit())) typeName = "Unit";
        else throw new RuntimeException("Unknown type for lambda param: " + lambdaType);

        return "(\\" + lambda.paramName + ": " + typeName + " ->\n" +
                getNextIndent() + indent().pprint(lambda.body) + "\n" +
                getIndent() + ")";
    }

    @Override
    public String visit(IRThunk thunk) {
        return "#thunk " + pprint(thunk.comp);
    }

    @Override
    public String visit(IRPush push) {
        return "#push " + pprint(push.value) + ";\n" +
            getIndent() + pprint(push.then);
    }

    @Override
    public String visit(IRForce force) {
        return "#force " + pprint(force.thunk);
    }

    @Override
    public String visit(IRName name) {
        return name.name;
    }

    @Override
    public String visit(IRString string) {
        return "\"" + string.value + "\"";
    }

    @Override
    public String visit(IRNumber number) {
        return "" + number.value;
    }
}
