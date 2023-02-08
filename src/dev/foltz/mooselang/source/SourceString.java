package dev.foltz.mooselang.source;

public class SourceString implements SourceDesc {
    public final String sourceName;
    public final String sourceCode;

    public SourceString(String name, String code) {
        this.sourceName = name;
        this.sourceCode = code;
    }

    @Override
    public String name() {
        return sourceName;
    }

    @Override
    public String code() {
        return sourceCode;
    }
}
