package dev.foltz.mooselang.parser;

import java.util.function.Function;

public interface Parser<T> {
    ParserState<T> run(ParserState<?> state);

    default <U> Parser<U> map(Function<T, U> fmap) {
        return s -> {
            var state = run(s);
            if (state.isError) {
                return state.error();
            }
            return state.success(state.index, fmap.apply(state.result));
        };
    }

    default Parser<T> mapError(Function<ParserState<T>, String> emap) {
        return s -> {
            var state = run(s);
            if (!state.isError) {
                return state;
            }
            return ParserState.error(state.source, state.index, emap.apply(state));
        };
    }

    default <U> Parser<U> mapState(Function<ParserState<T>, ParserState<U>> smap) {
        return s -> {
            var state = run(s);
            if (state.isError) {
                return state.error();
            }
            return smap.apply(state);
        };
    }
}
