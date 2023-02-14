package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.IRNode;
import dev.foltz.mooselang.ir.nodes.builtin.IRBuiltin;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.comp.IRLambda;
import dev.foltz.mooselang.ir.nodes.comp.IRProduce;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.ir.nodes.value.IRString;
import dev.foltz.mooselang.ir.nodes.value.IRThunk;
import dev.foltz.mooselang.ir.nodes.value.IRUnit;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.comp.CompLambda;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.value.*;

import java.util.Map;

public class Builtins {
    private static IRThunk curry(IRBuiltin builtin, Map<String, TypeValue> typedParams) {
        // U(A -> F(...))
        // F(InnerType)
        IRThunk wrapped = null;
        int apps = 0;
        for (var entry : typedParams.entrySet()) {
            var name = entry.getKey();
            var type = entry.getValue();
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
        System.out.println("BUILTIN PRINTING: " + interp.scope.find("_print_0").get());
        return new Interpreter(new IRProduce(new IRUnit()), interp.stack, interp.scope, false);
    });

    public static final IRThunk PRINT = curry(PRINT_BUILTIN, Map.of("_print_0", new ValueString()));

    private static final IRBuiltin ADD_BUILTIN = new IRBuiltin("add", new CompProducer(new ValueNumber()), interp -> {
        var ma = interp.scope.find("_add_0").get();
        var mb = interp.scope.find("_add_1").get();
        if (ma instanceof IRNumber na && mb instanceof IRNumber nb) {
            double value = na.value + nb.value;
            return new Interpreter(new IRProduce(new IRNumber(value)), interp.stack, interp.scope, false);
        }
        else {
            System.err.println("(+) expects arguments of number, received: " + ma + ", " + mb);
            return new Interpreter(interp.term, interp.stack, interp.scope, true);
        }
    });

    public static final IRThunk ADD = curry(ADD_BUILTIN, Map.of("_add_0", new ValueNumber(), "_add_1", new ValueNumber()));

//    public static final IRThunk ADD_THUNK =
//            new IRThunk(new IRLambda("_add_1", new ValueNumber(),
//                    new IRProduce(new IRThunk(new IRLambda("_add_2", new ValueNumber(),
//                            ADD_BUILTIN)))));
//    public static final ValueThunk ADD_TYPE =
//            new ValueThunk(new CompLambda("_add_1", new ValueNumber(),
//                    new CompProducer(new ValueThunk(new CompLambda("_add_2", new ValueNumber(),
//                            new CompProducer(new ValueNumber()))))));
    public static final IRBuiltin NUM2STR_BUILTIN = new IRBuiltin("num2str", new CompProducer(new ValueString()), interp -> {
        var ma = interp.scope.find("_num2str_0").get();
        if (ma instanceof IRNumber number) {
            return new Interpreter(new IRProduce(new IRString("" + number.value)), interp.stack, interp.scope, false);
        }
        else {
            System.err.println("num2str expects argument of number, received: " + ma);
            return new Interpreter(interp.term, interp.stack, interp.scope, true);
        }
    });

    public static final IRThunk NUM2STR = curry(NUM2STR_BUILTIN, Map.of("_num2str_0", new ValueNumber()));

//    public static final IRThunk NUM2STR_THUNK =
//            new IRThunk(new IRLambda("_num2str_1", new ValueNumber(),
//                    NUM2STR_BUILTIN));
//    public static final ValueThunk NUM2STR_TYPE =
//            new ValueThunk(new CompLambda("_num2str_1", new ValueNumber(),
//                    new CompProducer(new ValueString())));
}
