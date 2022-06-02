package dev.foltz.mooselang.parser.parsers;

import dev.foltz.mooselang.parser.IParser;
import dev.foltz.mooselang.parser.ParseResult;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.parser.ParseResult.failure;
import static dev.foltz.mooselang.parser.ParseResult.success;

public class ParserCombinators {
    public static IParser<Token> expect(TokenType type, String value) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + type + ", " + value + ") failed, state is empty");
            }

            Token token = state.peek();
            if (token.type == type && token.value.equals(value)) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + type + ", " + value + ") failed, next token is " + token);
        };
    }

    public static IParser<Token> expect(String value) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + value + ") failed, state is empty.");
            }

            Token token = state.peek();
            if (value.equals(token.value)) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + value +") failed, next token is " + token);
        };
    }

    public static IParser<Token> expect(TokenType type) {
        return state -> {
            if (state.isEmpty()) {
                return failure(state, "expect(" + type.name() + ") failed, state is empty.");
            }

            Token token = state.peek();
            if (token.type == type) {
                return success(state.next(), token);
            }

            return failure(state, "expect(" + type.name() + ") failed, next token is " + token);
        };
    }

    public static <T> IParser<T> optional(IParser<T> p) {
        return state -> {
            var r = p.parse(state);
            return r.isSuccess() ? r : success(state, null);
        };
    }

    public static IParser<List<?>> sequence(IParser<?> ...parsers) {
        return state -> {
            List<Object> results = new ArrayList<>();
            ParseResult<?> r = success(state, null);
            for (IParser<?> p : parsers) {
                r = p.parse(r.state);
                if (r.failed()) {
                    return failure(r.state, "sequence(...) failed: " + r.getMsg());
                }
                results.add(r.get());
            }
            return success(r.state, results);
        };
    }

    public static IParser<Object> any(IParser<?> ...parsers) {
        return state -> {
            for (IParser<?> p : parsers) {
                var r = p.parse(state);
                if (r.isSuccess()) {
                    return success(r.state, r.get());
                }
            }
            return failure(state, "any(...) failed");
        };
    }

    public static IParser<List<?>> many(IParser<?> parser) {
        return state -> {
            List<Object> results = new ArrayList<>();
            var r = parser.parse(state);
            while (r.isSuccess()) {
                results.add(r.get());
                r = parser.parse(r.state);
            }
            return success(r.state, results);
        };
    }

    public static IParser<List<?>> all(IParser<?> parser) {
        return state -> {
            List<Object> results = new ArrayList<>();
            var r = parser.parse(state);
            while (r.isSuccess()) {
                results.add(r.get());
                r = parser.parse(r.state);
            }
            if (r.failed() && r.state.isEmpty()) {
                return success(r.state, results);
            }
            return failure(r.state, "all(...) failed: " + r.getMsg());
        };
    }

    public static IParser<List<?>> many1(IParser<?> parser) {
        return sequence(parser, many(parser)).map(objs -> {
            List<Object> ls = new ArrayList<>();
            ls.add(objs.get(0));
            ls.addAll((List<?>) objs.get(1));
            return ls;
        });
    }

    public static <T> IParser<List<T>> sepBy0(IParser<T> parser, IParser<?> sep) {
        return optional(
            any(
                sepBy1(parser, sep),
                parser.map(List::of)
            ).map(ls -> (List<T>) ls)
        ).map(ls -> ls == null ? List.of() : ls);
    }

    public static <T> IParser<List<T>> sepBy1(IParser<T> parser, IParser<?> sep) {
        var sepThenP = sequence(sep, parser).map(objs -> (T) objs.get(1));
        return sequence(parser, many(sepThenP))
            .map(objs -> {
                List<T> results = new ArrayList<>();
                results.add((T) objs.get(0));
                results.addAll((List<T>) objs.get(1));
                return results;
            });
    }

    public static <T> IParser<List<T>> sepBy2(IParser<T> parser, IParser<?> sep) {
        return sequence(
            parser,
            sep,
            sepBy1(parser, sep)
        ).map(objs -> {
            var first = (T) objs.get(0);
            var rest = (List<T>) objs.get(2);
            ArrayList<T> values = new ArrayList<>(rest);
            values.add(0, first);
            return List.copyOf(values);
        });
    }
}
