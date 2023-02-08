package dev.foltz.mooselang.source;

public class SourceFile implements SourceDesc {
    public final String sourcePath;
    public final String sourceCode;

    public SourceFile(String path, String code) {
        this.sourcePath = path;
        this.sourceCode = code;
    }

    @Override
    public String name() {
        return sourcePath;
    }

    @Override
    public String code() {
        return sourceCode;
    }
}
