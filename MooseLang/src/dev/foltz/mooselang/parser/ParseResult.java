package dev.foltz.mooselang.parser;

import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class ParseResult<T> {
    public final ParseState state;

    public ParseResult(ParseState state) {
        this.state = state;
    }

    public abstract T get();

    public abstract String getMsg();

    public abstract <R> ParseResult<R> map(Function<T, R> f);

    public abstract boolean isSuccess();

    public boolean failed() {
        return !isSuccess();
    }

    public static <T> ParseResult<T> success(ParseState state, T value) {
        return new Success<>(state, value);
    }

    public static <T> ParseResult<T> failure(ParseState state, String errorMsg) {
        return new Failure<>(state, errorMsg);
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

        @Override
        public String getMsg() {
            return "Successful";
        }
    }

    public static final class Failure<T> extends ParseResult<T> {
        public final String errorMsg;

        private Failure(ParseState state, String errorMsg) {
            super(state);
            this.errorMsg = errorMsg;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public <R> ParseResult<R> map(Function<T, R> f) {
            return failure(state, errorMsg);
        }

        @Override
        public T get() {
            throw new NoSuchElementException("get() on failed parse result.");
        }

        @Override
        public String getMsg() {
            return errorMsg;
        }
    }
}
