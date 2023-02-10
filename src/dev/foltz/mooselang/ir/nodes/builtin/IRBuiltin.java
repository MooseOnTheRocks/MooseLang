package dev.foltz.mooselang.ir.nodes.builtin;

import dev.foltz.mooselang.ir.VisitorIR;
import dev.foltz.mooselang.ir.nodes.comp.IRComp;
import dev.foltz.mooselang.ir.nodes.comp.IRLambda;
import dev.foltz.mooselang.ir.nodes.comp.IRProduce;
import dev.foltz.mooselang.ir.nodes.value.IRThunk;
import dev.foltz.mooselang.ir.nodes.value.IRNumber;
import dev.foltz.mooselang.ir.nodes.value.IRString;
import dev.foltz.mooselang.ir.nodes.value.IRUnit;
import dev.foltz.mooselang.rt.Interpreter;
import dev.foltz.mooselang.typing.comp.CompLambda;
import dev.foltz.mooselang.typing.comp.CompProducer;
import dev.foltz.mooselang.typing.value.ValueNumber;
import dev.foltz.mooselang.typing.value.ValueString;
import dev.foltz.mooselang.typing.value.ValueThunk;
import dev.foltz.mooselang.typing.value.ValueUnit;

import java.util.function.Function;

public class IRBuiltin extends IRComp {
    public final String name;
    public final Function<Interpreter, Interpreter> internal;

    public IRBuiltin(String name, Function<Interpreter, Interpreter> internal) {
        this.name = name;
        this.internal = internal;
    }

    @Override
    public <T> T apply(VisitorIR<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "IRBuiltin(" + name + ")";
    }

    private static final IRBuiltin PRINT_BUILTIN = new IRBuiltin("print", interp -> {
        System.out.println("BUILTIN PRINTING: " + interp.scope.find("_print_1").get());
        return new Interpreter(new IRProduce(new IRUnit()), interp.stack, interp.scope, false);
    });

    public static final IRThunk PRINT_THUNK =
        new IRThunk(new IRLambda("_print_1", new ValueString(),
            PRINT_BUILTIN));
    public static final ValueThunk PRINT_TYPE =
        new ValueThunk(new CompLambda("_print_1", new ValueString(),
            new CompProducer(new ValueUnit())));

    private static final IRBuiltin ADD_BUILTIN = new IRBuiltin("add", interp -> {
        var ma = interp.scope.find("_add_1").get();
        var mb = interp.scope.find("_add_2").get();
        if (ma instanceof IRNumber na && mb instanceof IRNumber nb) {
            double value = na.value + nb.value;
            return new Interpreter(new IRProduce(new IRNumber(value)), interp.stack, interp.scope, false);
        }
        else {
            System.err.println("(+) expects arguments of number, received: " + ma + ", " + mb);
            return new Interpreter(interp.term, interp.stack, interp.scope, true);
        }
    });

    public static final IRThunk ADD_THUNK =
        new IRThunk(new IRLambda("_add_1", new ValueNumber(),
                new IRProduce(new IRThunk(new IRLambda("_add_2", new ValueNumber(),
                        ADD_BUILTIN)))));
    public static final ValueThunk ADD_TYPE =
        new ValueThunk(new CompLambda("_add_1", new ValueNumber(),
            new CompProducer(new ValueThunk(new CompLambda("_add_2", new ValueNumber(),
                new CompProducer(new ValueNumber()))))));
    public static final IRBuiltin NUM2STR_BUILTIN = new IRBuiltin("num2str", interp -> {
        var ma = interp.scope.find("_num2str_1").get();
        if (ma instanceof IRNumber number) {
            return new Interpreter(new IRProduce(new IRString("" + number.value)), interp.stack, interp.scope, false);
        }
        else {
            System.err.println("num2str expects argument of number, received: " + ma);
            return new Interpreter(interp.term, interp.stack, interp.scope, true);
        }
    });

    public static final IRThunk NUM2STR_THUNK =
        new IRThunk(new IRLambda("_num2str_1", new ValueNumber(),
            NUM2STR_BUILTIN));
    public static final ValueThunk NUM2STR_TYPE =
        new ValueThunk(new CompLambda("_num2str_1", new ValueNumber(),
            new CompProducer(new ValueString())));
}
