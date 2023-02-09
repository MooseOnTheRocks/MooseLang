package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.parser.BasicParsers;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;

import static dev.foltz.mooselang.parser.BasicParsers.match;

public class ParserIR {
    public static final Parser<IRValue> irValue = ParserIR::irValue;
    public static final Parser<IRName> irName = BasicParsers.name.map(IRName::new);
    public static final Parser<IRUnit> irUnit = match("()").map(u -> new IRUnit());
    public static final Parser<IRNumber> irNumber = null;
    public static final Parser<IRString> irString = null;

    public static final Parser<IRComp> irComp = ParserIR::irComp;
    public static final Parser<IRDo> irDoComp = null;
    public static final Parser<IRForce> irForce = null;
    public static final Parser<IRLambda> irLambda = null;
    public static final Parser<IRLet> irLetValue = null;
    public static final Parser<IRPush> irPush = null;
    public static final Parser<IRThunk> irThunk = null;

    public static ParserState<IRValue> irValue(ParserState<?> s) {
        return null;
    }

    public static ParserState<IRComp> irComp(ParserState<?> s) {
        return null;
    }
}
