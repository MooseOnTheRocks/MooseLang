package dev.foltz.mooselang.parser;

import java.util.function.Function;

@FunctionalInterface
public interface IParser<T> {
    ParseResult<T> parse(ParseState state);

    default <R> IParser<R> map(Function<T, R> f) {
        return state -> parse(state).map(f);
    }
}
