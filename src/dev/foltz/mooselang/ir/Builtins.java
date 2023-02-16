package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.IRLambda;
import dev.foltz.mooselang.ir.nodes.comp.IRProduce;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.ir.nodes.value.IRString;
import dev.foltz.mooselang.ir.nodes.value.IRThunk;
import dev.foltz.mooselang.ir.nodes.value.IRUnit;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.rt.InterpreterOld;
import dev.foltz.mooselang.typing.TypeBase;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.value.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Builtins {
    private static IRThunk curry(IRBuiltin builtin, List<String> paramNames, List<TypeValue> paramTypes) {
        if (paramNames.size() != paramTypes.size()) {
            throw new RuntimeException("curry with different sized name and type lists: " + paramNames + ", " + paramTypes);
        }

        // U(A -> F(...))
        // F(InnerType)
        IRThunk wrapped = null;
        int apps = 0;
//        var paramEntries = new ArrayList<>(typedParams.entrySet().stream().toList());
//        Collections.reverse(paramEntries);
        for (int i = paramNames.size() - 1; i >= 0; i--) {
            var name = paramNames.get(i);
            var type = paramTypes.get(i);
            if (apps == 0) {
                wrapped = new IRThunk(new IRLambda(name, type, builtin));
            }
            else {
                wrapped = new IRThunk(new IRLambda(name, type, new IRProduce(wrapped)));
            }
            apps += 1;
        }
        return wrapped;
    }

    private static final IRBuiltin PRINT_BUILTIN = new IRBuiltin("print", new CompProducer(new ValueUnit()), interp -> {
        System.out.println("BUILTIN PRINTING: " + interp.find("_print_0"));
//        return new Interpreter(new IRProduce(new IRUnit()), interp.context, interp.stack, false);
        return interp.withTerm(new IRProduce(new IRUnit()));
    });

    public static final IRThunk PRINT = curry(PRINT_BUILTIN, List.of("_print_0"), List.of(new ValueString()));

    private static final IRBuiltin ADD_BUILTIN = new IRBuiltin("add", new CompProducer(new ValueNumber()), interp -> {
        var ma = interp.find("_add_0");
        var mb = interp.find("_add_1");
        if (ma instanceof IRNumber na && mb instanceof IRNumber nb) {
            double value = na.value + nb.value;
//            return new Interpreter(new IRProduce(new IRNumber(value)), interp.context, false);
            return interp.withTerm(new IRProduce(new IRNumber(value)));
        }
        else {
//            System.err.println("(+) expects arguments of number, received: " + ma + ", " + mb);
//            return new Interpreter(interp.term, interp.context, true);
            return interp.error("(+) expects arguments of number, received: " + ma + ", " + mb);
        }
    });

//    public static final IRThunk ADD = curry(ADD_BUILTIN, Map.of("_add_0", new ValueNumber(), "_add_1", new ValueNumber()));
    public static final IRThunk ADD = curry(ADD_BUILTIN, List.of("_add_0", "_add_1"), List.of(new ValueNumber(), new ValueNumber()));

    private static final IRBuiltin SUBTRACT_BUILTIN = new IRBuiltin("subtract", new CompProducer(new ValueNumber()), interp -> {
        var ma = interp.find("_subtract_0");
        var mb = interp.find("_subtract_1");
        if (ma instanceof IRNumber na && mb instanceof IRNumber nb) {
            double value = na.value - nb.value;
//            return new Interpreter(new IRProduce(new IRNumber(value)), interp.context, false);
            return interp.withTerm(new IRProduce(new IRNumber(value)));
        }
        else {
//            System.err.println("(-) expects arguments of number, received: " + ma + ", " + mb);
//            return new Interpreter(interp.term, interp.context, true);
            return interp.error("(-) expects arguments of number, received: " + ma + ", " + mb);

        }
    });

//    public static final IRThunk SUBTRACT = curry(SUBTRACT_BUILTIN, Map.of("_subtract_0", new ValueNumber(), "_subtract_1", new ValueNumber()));
    public static final IRThunk SUBTRACT = curry(SUBTRACT_BUILTIN, List.of("_subtract_0", "_subtract_1"), List.of(new ValueNumber(), new ValueNumber()));

    private static final IRBuiltin MULTIPLY_BUILTIN = new IRBuiltin("multiply", new CompProducer(new ValueNumber()), interp -> {
        var ma = interp.find("_multiply_0");
        var mb = interp.find("_multiply_1");
        if (ma instanceof IRNumber na && mb instanceof IRNumber nb) {
            double value = na.value * nb.value;
//            return new Interpreter(new IRProduce(new IRNumber(value)), interp.context, false);
            return interp.withTerm(new IRProduce(new IRNumber(value)));
        }
        else {
//            System.err.println("(*) expects arguments of number, received: " + ma + ", " + mb);
//            return new Interpreter(interp.term, interp.context, true);
            return interp.error("(*) expects arguments of number, received: " + ma + ", " + mb);

        }
    });

//    public static final IRThunk MULTIPLY = curry(MULTIPLY_BUILTIN, Map.of("_multiply_0", new ValueNumber(), "_multiply_1", new ValueNumber()));
    public static final IRThunk MULTIPLY = curry(MULTIPLY_BUILTIN, List.of("_multiply_0", "_multiply_1"), List.of(new ValueNumber(), new ValueNumber()));

//    public static final IRThunk ADD_THUNK =
//            new IRThunk(new IRLambda("_add_1", new ValueNumber(),
//                    new IRProduce(new IRThunk(new IRLambda("_add_2", new ValueNumber(),
//                            ADD_BUILTIN)))));
//    public static final ValueThunk ADD_TYPE =
//            new ValueThunk(new CompLambda("_add_1", new ValueNumber(),
//                    new CompProducer(new ValueThunk(new CompLambda("_add_2", new ValueNumber(),
//                            new CompProducer(new ValueNumber()))))));
    public static final IRBuiltin NUM2STR_BUILTIN = new IRBuiltin("num2str", new CompProducer(new ValueString()), interp -> {
        var ma = interp.find("_num2str_0");
        if (ma instanceof IRNumber number) {
//            return new Interpreter(new IRProduce(new IRString("" + number.value)), interp.context, false);
            return interp.withTerm(new IRProduce(new IRString("" + number.value)));
        }
        else {
//            System.err.println("num2str expects argument of number, received: " + ma);
//            return new Interpreter(interp.term, interp.context, true);
            return interp.error("num2str expects argument of number, received: " + ma);

        }
    });

//    public static final IRThunk NUM2STR = curry(NUM2STR_BUILTIN, Map.of("_num2str_0", new ValueNumber()));
    public static final IRThunk NUM2STR = curry(NUM2STR_BUILTIN, List.of("_num2str_0"), List.of(new ValueNumber()));

//    public static final IRThunk NUM2STR_THUNK =
//            new IRThunk(new IRLambda("_num2str_1", new ValueNumber(),
//                    NUM2STR_BUILTIN));
//    public static final ValueThunk NUM2STR_TYPE =
//            new ValueThunk(new CompLambda("_num2str_1", new ValueNumber(),
//                    new CompProducer(new ValueString())));
}
