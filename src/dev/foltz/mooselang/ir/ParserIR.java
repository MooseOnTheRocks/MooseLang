package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.List;
import java.util.Map;

import static dev.foltz.mooselang.parser.Parsers.match;
import static dev.foltz.mooselang.parser.Parsers.name;
import static dev.foltz.mooselang.parser.ParserCombinators.*;
import static dev.foltz.mooselang.parser.Parsers.anyws;

public class ParserIR {
    public static final Parser<IRValue> irValue = ParserIR::irValue;
    public static final Parser<IRComp> irComp = ParserIR::irComp;

    // Values
    public static final Parser<IRValue> irParenValue = all(match("("), anyws, irValue, anyws, match(")")).map(ls -> (IRValue) ls.get(2));
    public static final Parser<IRValueName> irName = any(Parsers.name, Parsers.symbolic).map(n -> (String) n).map(IRValueName::new);
    public static final Parser<IRValueUnit> irUnit = match("()").map(u -> new IRValueUnit());
    public static final Parser<IRValueNumber> irNumber = Parsers.number.map(IRValueNumber::new);
    public static final Parser<IRValueString> irString = Parsers.string.map(IRValueString::new);
    public static final Parser<IRValueTuple> irTuple =
        all(match("("),
            anyws,
            joining(all(anyws, match(","), anyws), irValue),
            anyws,
            match(")"))
        .map(ls -> (List<IRValue>) ls.get(2))
        .map(IRValueTuple::new);
    public static final Parser<IRValueThunk> irThunk = all(match("#thunk"), anyws, irComp).map(ls -> new IRValueThunk((IRComp) ls.get(2), Map.of()));

    // Computations
    public static final Parser<IRComp> irParenComp = all(match("("), anyws, irComp, anyws, match(")")).map(ls -> (IRComp) ls.get(2));
    public static final Parser<IRCompProduce> irProduce = all(match("#produce"),  anyws, irValue).map(ls -> new IRCompProduce((IRValue) ls.get(2)));
    public static final Parser<IRCompForce> irForce = all(match("#force"), anyws, irValue).map(ls -> new IRCompForce((IRValue) ls.get(2)));
    public static final Parser<IRCompDo> irDo =
        intersperse(anyws,
            match("do"),
            irComp,
            match("="),
            name,
            match("in"),
            irComp)
        .map(ls -> new IRCompDo(
            (String) ls.get(3),
            (IRComp) ls.get(1),
            (IRComp) ls.get(5)));

    public static final Parser<IRCompLambda> irLambda =
        intersperse(anyws,
            match("\\"),
            name,
            match(":"),
            name,
            match("->"),
            irComp)
        .map(ls -> new IRCompLambda(
            (String) ls.get(1),
            TypeValue.fromString((String) ls.get(3)),
            (IRComp) ls.get(5)));

    public static final Parser<IRCompLet> irLet =
        intersperse(anyws,
            match("let"),
            name,
            match("="),
            irValue,
            match("in"),
            irComp)
        .map(ls -> new IRCompLet(
            (String) ls.get(1),
            (IRValue) ls.get(3),
            (IRComp) ls.get(5)));

    public static final Parser<IRCompPush> irPush =
        all(match("#push"),
            anyws,
            irValue,
            anyws,
            irComp)
        .map(ls -> new IRCompPush(
            (IRValue) ls.get(2),
            (IRComp) ls.get(4)));

    public static final Parser<IRCompCaseOfBranch> irCaseOfBranch =
        intersperse(anyws,
            irValue,
            match("->"),
            irComp)
        .map(ls -> new IRCompCaseOfBranch(
            (IRValue) ls.get(0),
            (IRComp) ls.get(2)
        ));

    public static final Parser<IRCompCaseOf> irCaseOf =
        intersperse(anyws,
            match("case"),
            irValue,
            match("of"),
            joining(anyws,
                any(irCaseOfBranch,
                    intersperse(anyws, match("("), irCaseOfBranch, match(")"))
                    .map(ls -> ls.get(1)))))
        .map(ls -> new IRCompCaseOf(
            (IRValue) ls.get(1),
            (List<IRCompCaseOfBranch>) ls.get(3)));

    private static ParserState<IRValue> irValue(ParserState<?> s) {
        return any(
                irTuple,
                irParenValue,
                irName,
                irNumber,
                irString,
                irThunk,
                irUnit)
            .map(v -> (IRValue) v).run(s);
    }

    private static ParserState<IRComp> irComp(ParserState<?> s) {
        return any(
                irCaseOf,
                irParenComp,
                irDo,
                irForce,
                irLambda,
                irLet,
                irPush,
                irProduce)
            .map(c -> (IRComp) c).run(s);
    }
}
