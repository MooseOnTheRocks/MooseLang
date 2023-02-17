package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.comp.IRCompBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.IRCompLambda;
import dev.foltz.mooselang.ir.nodes.comp.IRCompProduce;
import dev.foltz.mooselang.ir.nodes.value.IRValueNumber;
import dev.foltz.mooselang.ir.nodes.value.IRValueString;
import dev.foltz.mooselang.ir.nodes.value.IRValueThunk;
import dev.foltz.mooselang.ir.nodes.value.IRValueUnit;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.value.*;

import java.util.List;
import java.util.Map;

public class Builtins {
    public static final IRValueThunk PRINT;
    public static final IRValueThunk NUM2STR;
    public static final IRValueThunk ADD;
    public static final IRValueThunk SUBTRACT;
    public static final IRValueThunk MULTIPLY;

    static {
        PRINT = curry(List.of("_print_0"), List.of(new ValueString()), new IRCompBuiltin("print", new CompProducer(new ValueUnit()),
            interp -> {
                var value = interp.resolve(interp.find("_print_0"));
                var str = value.toString();
                if (value instanceof IRValueString string) str = string.value;
                else if (value instanceof IRValueNumber number) str = "" + number.value;
                else if (value instanceof IRValueUnit) str = "()";
                System.out.println(str);
                return interp.withTerm(new IRCompProduce(new IRValueUnit()));
            }));

        NUM2STR = curry(List.of("_num2str_0"), List.of(new ValueNumber()), new IRCompBuiltin("num2str", new CompProducer(new ValueString()),
                interp -> interp.find("_num2str_0") instanceof IRValueNumber number
                        ? interp.withTerm(new IRCompProduce(new IRValueString("" + number.value)))
                        : interp.error("")));

        ADD = curry(List.of("_add_0", "_add_1"), List.of(new ValueNumber(), new ValueNumber()), new IRCompBuiltin("add", new CompProducer(new ValueNumber()),
                interp -> interp.find("_add_0") instanceof IRValueNumber a && interp.find("_add_1") instanceof IRValueNumber b
                        ? interp.withTerm(new IRCompProduce(new IRValueNumber(a.value + b.value)))
                        : interp.error("(+) expects arguments of Number, received: " + interp.find("_add_0") + " + " + interp.find("_add_1"))));

        SUBTRACT = curry(List.of("_subtract_0", "_subtract_1"), List.of(new ValueNumber(), new ValueNumber()), new IRCompBuiltin("subtract", new CompProducer(new ValueNumber()),
                interp -> interp.find("_subtract_0") instanceof IRValueNumber a && interp.find("_subtract_1") instanceof IRValueNumber b
                        ? interp.withTerm(new IRCompProduce(new IRValueNumber(a.value + b.value)))
                        : interp.error("(-) expects arguments of Number, received: " + interp.find("_subtract_0") + " + " + interp.find("_subtract_1"))));

        MULTIPLY = curry(List.of("_multiply_0", "_multiply_1"), List.of(new ValueNumber(), new ValueNumber()), new IRCompBuiltin("multiply", new CompProducer(new ValueNumber()),
                interp -> interp.find("_multiply_0") instanceof IRValueNumber a && interp.find("_multiply_1") instanceof IRValueNumber b
                        ? interp.withTerm(new IRCompProduce(new IRValueNumber(a.value + b.value)))
                        : interp.error("(*) expects arguments of Number, received: " + interp.find("_multiply_0") + " + " + interp.find("_multiply_1"))));
    }

    private static IRValueThunk curry(List<String> paramNames, List<TypeValue> paramTypes, IRCompBuiltin builtin) {
        if (paramNames.size() != paramTypes.size()) {
            throw new RuntimeException("curry with different sized name and type lists: " + paramNames + ", " + paramTypes);
        }

        // U(A -> F(...))
        // F(InnerType)
        IRValueThunk wrapped = null;
        int apps = 0;
//        var paramEntries = new ArrayList<>(typedParams.entrySet().stream().toList());
//        Collections.reverse(paramEntries);
        for (int i = paramNames.size() - 1; i >= 0; i--) {
            var name = paramNames.get(i);
            var type = paramTypes.get(i);
            if (apps == 0) {
                wrapped = new IRValueThunk(new IRCompLambda(name, type, builtin), Map.of());
            }
            else {
                wrapped = new IRValueThunk(new IRCompLambda(name, type, new IRCompProduce(wrapped)), Map.of());
            }
            apps += 1;
        }
        return wrapped;
    }
}
