package dev.foltz.mooselang.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public interface SourceDesc {
    String name();
    String code();

    static void saveAsFile(SourceDesc source, String first, String... more) {
        Path absPath = Path.of(first, more).toAbsolutePath();
        try {
            Files.createDirectories(absPath.getParent());
            Files.write(absPath, List.of(source.code().split("\n")), StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + absPath);
        }
    }

    static SourceFile fromFile(String first, String... more) {
        Path absPath = Path.of(first, more).toAbsolutePath();
        try {
            String content = Files.readString(absPath);
            return new SourceFile(absPath.toString(), content);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read source file: " + absPath);
        }
    }

    static SourceString fromString(String name, String str) {
        return new SourceString(name, str);
    }
}
