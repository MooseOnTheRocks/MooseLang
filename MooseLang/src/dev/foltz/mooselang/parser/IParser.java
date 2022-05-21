package dev.foltz.mooselang.parser;

import java.util.function.Function;

import static dev.foltz.mooselang.parser.ParseResult.failure;

@FunctionalInterface
public interface IParser<T> {
    ParseResult<T> parse(ParseState state);

    default <R> IParser<R> map(Function<T, R> f) {
        return state -> parse(state).map(f);
    }

    default IParser<T> mapErrorMsg(Function<String, String> f) {
        return state -> {
            var res = parse(state);
            return res.failed() ? failure(res.state, f.apply(res.getMsg())) : res;
        };
    }
}
