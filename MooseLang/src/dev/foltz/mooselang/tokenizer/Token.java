package dev.foltz.mooselang.tokenizer;

public class Token {
    public final TokenType type;
    public final String value;

    public final int rangeFrom, rangeTo;
    public final String sourceMatch;

    public Token(TokenType type, String value, int rangeFrom, int rangeTo, String sourceMatch) {
        this.type = type;
        this.value = value;

        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.sourceMatch = sourceMatch;
    }

    public String toString() {
        return "[" + this.type.name() + ", '" + value + "']";
    }
}
