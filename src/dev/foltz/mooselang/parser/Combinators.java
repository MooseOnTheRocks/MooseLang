package dev.foltz.mooselang.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Combinators {
    public static <T> Parser<T> defaulted(T defaultValue, Parser<T> parser) {
        return s -> {
            var state = parser.run(s);
            if (state.isError) {
                return s.success(s.index, defaultValue);
            }
            return state;
        };
    }

    public static <T> Parser<Optional<T>> optional(Parser<T> parser) {
        return s -> {
            var state = parser.run(s);
            if (state.isError) {
                return s.success(s.index, Optional.empty());
            }
            return state.success(state.index, Optional.of(state.result));
        };
    }
    public static <T> Parser<List<T>> many1(Parser<T> parser) {
        return s -> {
            var state = parser.run(s);
            if (state.isError) {
                return state.error();
            }
            return many(parser).map(ls -> { ls.add(0, state.result); return ls; }).run(state);
        };
    }
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return s -> {
            if (s.isError) {
                return s.error();
            }

            List<T> results = new ArrayList<>();
            var prevState = s;
            var state = parser.run(s);
            while (!state.isError && state.index != prevState.index) {
                results.add(state.result);
                prevState = state;
                state = parser.run(state);
            }

            return prevState.success(prevState.index, results);
        };
    }

    public static Parser<?> any(Parser<?>... parsers) {
        return s -> {
            if (s.isError) {
                return s.error();
            }

            List<ParserState<?>> allMatches = new ArrayList<>();
            for (Parser<?> p : parsers) {
                var nextState = p.run(s);
                if (!nextState.isError) {
                    var res = nextState.success(nextState.index, nextState.result);
                    allMatches.add(res);
                }
            }

            if (allMatches.isEmpty()) {
                return s.error();
            }

            int longestMatch = 0;
            for (int i = 1; i < allMatches.size(); i++) {
                if (allMatches.get(i).index > allMatches.get(longestMatch).index) {
                    longestMatch = i;
                }
            }

            return (ParserState<Object>) allMatches.get(longestMatch);
        };
    }

    public static Parser<List<?>> all(Parser<?>... parsers) {
        return s -> {
            List<Object> results = new ArrayList<>();

            var state = s;
            if (state.isError) {
                return state.error();
            }

            for (Parser<?> p : parsers) {
                state = p.run(state);
                if (state.isError) {
                    return state.error();
                }
                results.add(state.result);
            }

            return state.success(state.index, List.copyOf(results));
        };
    }
}
