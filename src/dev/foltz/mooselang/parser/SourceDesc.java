package dev.foltz.mooselang.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface SourceDesc {
    String name();
    String code();

    static SourceDesc fromFile(String first, String... more) {
        Path absPath = Path.of(first, more).toAbsolutePath();
        try {
            String content = Files.readString(absPath);
            return new SourceFile(absPath.toString(), content);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read source file: " + absPath);
        }
    }

    static SourceDesc fromString(String name, String str) {
        return new SourceString(name, str);
    }
}
