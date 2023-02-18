package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRDefType;
import dev.foltz.mooselang.ir.nodes.IRDefValue;
import dev.foltz.mooselang.ir.nodes.IRModule;
import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.type.IRTypeName;
import dev.foltz.mooselang.ir.nodes.type.IRTypeSum;
import dev.foltz.mooselang.ir.nodes.type.IRTypeTuple;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.typing.value.*;

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
    public String visit(IRValueAnnotated typed) {
        return "(" + pprint(typed.value) + ") : " + indent().pprint(typed.type);
    }

    @Override
    public String visit(IRTypeName name) {
        return name.name;
    }

    @Override
    public String visit(IRTypeTuple tuple) {
        return "(" + tuple.types.stream().map(inline()::pprint).collect(Collectors.joining(", ")) + ")";
    }

    @Override
    public String visit(IRModule module) {
        var sb = new StringBuilder();
        sb.append("-- Top Level Type Definitions\n\n");
        for (var topTypeDefs : module.topLevelTypeDefs.entrySet()) {
            sb.append("-- ");
            sb.append(pprint(topTypeDefs.getKey()));
            sb.append("\n");
            for (var typeDef : topTypeDefs.getValue()) {
                sb.append(pprint(typeDef));
                sb.append("\n\n");
            }
            sb.append("\n\n");
        }
        sb.append("-- Top Level Definitions\n\n");
        for (var topDefs : module.topLevelDefs.entrySet()) {
            sb.append("-- ");
            sb.append(pprint(topDefs.getKey()));
            sb.append("\n");
            for (var def : topDefs.getValue()) {
                sb.append(pprint(def));
                sb.append("\n\n");
            }
            sb.append("\n\n");
        }
        sb.append("-- Top Level Computations\n\n");
        for (var topComp : module.topLevelComps) {
            sb.append(pprint(topComp));
            sb.append("\n\n\n");
        }
        return sb.toString();
    }

    @Override
    public String visit(IRTypeSum sum) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sum.conNames.size(); i++) {
            if (i > 0) {
                sb.append("\n");
                sb.append(getIndent());
                sb.append("| ");
            }
            sb.append(sum.conNames.get(i));
            sb.append(" ");
            sb.append(sum.params.get(i).stream().map(t -> indent().pprint(t)).collect(Collectors.joining(" ")));
        }
        return sb.toString();
    }

    @Override
    public String visit(IRDefType defType) {
        return "type " + defType.name + "\n" + getNextIndent() + "= " + indent().pprint(defType.type);
    }

    @Override
    public String visit(IRDefValue globalDef) {
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

    private String typeToString(TypeValue value) {
        if (value instanceof ValueNumber) return "Number";
        else if (value instanceof ValueString) return "String";
        else if (value instanceof ValueUnit) return "()";
        else if (value instanceof TypeValueNamed named) return named.name;
        else if (value instanceof ValueTuple tuple)
            return "(" + tuple.values.stream().map(this::typeToString).collect(Collectors.joining(", ")) + ")";
        else throw new RuntimeException("Unknown type for conversion: " + value);
    }

    @Override
    public String visit(IRCompLambda lambda) {
        var lambdaType = lambda.paramType;
        var typeName = typeToString(lambdaType);

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
