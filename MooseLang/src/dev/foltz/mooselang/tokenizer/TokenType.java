package dev.foltz.mooselang.tokenizer;

public enum TokenType {
    T_WHITESPACE,
    T_NEWLINE,

    T_KW_FOR,
    T_KW_IN,

    T_NAME,
    T_NUMBER,
    T_STRING,

    T_EQUALS,
    T_COMMA,

    T_LPAREN,
    T_RPAREN,
    T_LBRACE,
    T_RBRACE,
    T_LBRACKET,
    T_RBRACKET,
}
