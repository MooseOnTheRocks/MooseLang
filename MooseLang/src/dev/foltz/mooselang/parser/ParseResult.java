package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.tokenizer.Token;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

public abstract class ParseResult<T> {
    public final ParseState state;

    public ParseResult(ParseState state) {
        this.state = state;
    }

    public abstract T get();

    public abstract <R> ParseResult<R> map(Function<T, R> f);

    public abstract boolean isSuccess();

    public boolean failed() {
        return !isSuccess();
    }

    public static <T> ParseResult<T> success(ParseState state, T value) {
        return new Success<>(state, value);
    }

    public static <T> ParseResult<T> failure(ParseState state) {
        return new Failure<>(state);
    }

    public static final class Success<T> extends ParseResult<T> {
        private final T value;

        private Success(ParseState state, T value) {
            super(state);
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <R> ParseResult<R> map(Function<T, R> f) {
            return success(state, f.apply(value));
        }

        @Override
        public T get() {
            return value;
        }
    }

    public static final class Failure<T> extends ParseResult<T> {
        private Failure(ParseState state) {
            super(state);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <R> ParseResult<R> map(Function<T, R> f) {
            return failure(state);
        }

        @Override
        public T get() {
            throw new NoSuchElementException("get() on failed parse result.");
        }
    }
}
