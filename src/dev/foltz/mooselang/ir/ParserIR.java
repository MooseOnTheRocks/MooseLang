package dev.foltz.mooselang.ir;

import dev.foltz.mooselang.ast.nodes.expr.ASTExpr;
import dev.foltz.mooselang.ast.nodes.expr.ExprCaseOf;
import dev.foltz.mooselang.ast.nodes.expr.ExprCaseOfBranch;
import dev.foltz.mooselang.ast.nodes.expr.ExprTuple;
import dev.foltz.mooselang.ir.nodes.comp.*;
import dev.foltz.mooselang.ir.nodes.value.*;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.Parser;
import dev.foltz.mooselang.parser.ParserState;
import dev.foltz.mooselang.typing.value.TypeValue;

import java.util.List;

import static dev.foltz.mooselang.parser.Parsers.match;
import static dev.foltz.mooselang.parser.Parsers.name;
import static dev.foltz.mooselang.parser.ParserCombinators.*;
import static dev.foltz.mooselang.parser.Parsers.anyws;

public class ParserIR {
    public static final Parser<IRValue> irValue = ParserIR::irValue;
    public static final Parser<IRComp> irComp = ParserIR::irComp;

    // Values
    public static final Parser<IRValue> irParenValue = all(match("("), anyws, irValue, anyws, match(")")).map(ls -> (IRValue) ls.get(2));
    public static final Parser<IRName> irName = any(Parsers.name, Parsers.symbolic).map(n -> (String) n).map(IRName::new);
    public static final Parser<IRUnit> irUnit = match("()").map(u -> new IRUnit());
    public static final Parser<IRNumber> irNumber = Parsers.number.map(IRNumber::new);
    public static final Parser<IRString> irString = Parsers.string.map(IRString::new);
    public static final Parser<IRTuple> irTuple =
        all(match("("),
            anyws,
            joining(all(anyws, match(","), anyws), irValue),
            anyws,
            match(")"))
        .map(ls -> (List<IRValue>) ls.get(2))
        .map(IRTuple::new);
    public static final Parser<IRThunk> irThunk = all(match("#thunk"), anyws, irComp).map(ls -> new IRThunk((IRComp) ls.get(2)));

    // Computations
    public static final Parser<IRComp> irParenComp = all(match("("), anyws, irComp, anyws, match(")")).map(ls -> (IRComp) ls.get(2));
    public static final Parser<IRProduce> irProduce = all(match("#produce"),  anyws, irValue).map(ls -> new IRProduce((IRValue) ls.get(2)));
    public static final Parser<IRForce> irForce = all(match("#force"), anyws, irValue).map(ls -> new IRForce((IRValue) ls.get(2)));
    public static final Parser<IRDo> irDo =
        intersperse(anyws,
            match("do"),
            irComp,
            match("="),
            name,
            match("in"),
            irComp)
        .map(ls -> new IRDo(
            (String) ls.get(3),
            (IRComp) ls.get(1),
            (IRComp) ls.get(5)));

    public static final Parser<IRLambda> irLambda =
        intersperse(anyws,
            match("\\"),
            name,
            match(":"),
            name,
            match("->"),
            irComp)
        .map(ls -> new IRLambda(
            (String) ls.get(1),
            TypeValue.fromString((String) ls.get(3)),
            (IRComp) ls.get(5)));

    public static final Parser<IRLet> irLet =
        intersperse(anyws,
            match("let"),
            name,
            match("="),
            irValue,
            match("in"),
            irComp)
        .map(ls -> new IRLet(
            (String) ls.get(1),
            (IRValue) ls.get(3),
            (IRComp) ls.get(5)));

    public static final Parser<IRPush> irPush =
        all(match("#push"),
            anyws,
            irValue,
            anyws,
            irComp)
        .map(ls -> new IRPush(
            (IRValue) ls.get(2),
            (IRComp) ls.get(4)));

    public static final Parser<IRCaseOfBranch> irCaseOfBranch =
        intersperse(anyws,
            irValue,
            match("->"),
            irComp)
        .map(ls -> new IRCaseOfBranch(
            (IRValue) ls.get(0),
            (IRComp) ls.get(2)
        ));

    public static final Parser<IRCaseOf> irCaseOf =
        intersperse(anyws,
            match("case"),
            irValue,
            match("of"),
            joining(anyws,
                any(irCaseOfBranch,
                    intersperse(anyws, match("("), irCaseOfBranch, match(")"))
                    .map(ls -> ls.get(1)))))
        .map(ls -> new IRCaseOf(
            (IRValue) ls.get(1),
            (List<IRCaseOfBranch>) ls.get(3)));

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
