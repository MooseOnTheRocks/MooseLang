package dev.foltz.mooselang.parser;

public class ParserState<T> {
    public final SourceDesc source;
    public final int index;
    public final boolean isError;
    public final String error;
    public final T result;

    public ParserState(SourceDesc source, int index, boolean isError, String error, T result) {
        this.source = source;
        this.index = index;
        this.isError = isError;
        this.error = null;
        this.result = result;
    }

    public String rem() {
        return source.code().substring(this.index);
    }

    public static <T> ParserState<T> error(SourceDesc source, int index, String error) {
        return new ParserState<>(source, index, true, error, null);
    }

    public static <T> ParserState<T> error(ParserState<?> prev) {
        return error(prev.source, prev.index, prev.error);
    }

    <U> ParserState<U> error(String msg) {
        return error(this.source, this.index, msg);
    }

    <U> ParserState<U> error() {
        return error(this);
    }

    public static <T> ParserState<T> success(SourceDesc source, int index, T result) {
        return new ParserState<>(source, index, false, null, result);
    }

    public static <T> ParserState<T> success(ParserState<?> prev, int index, T result) {
        return success(prev.source, index, result);
    }

    <U> ParserState<U> success(int index, U result) {
        return success(this, index, result);
    }
}
