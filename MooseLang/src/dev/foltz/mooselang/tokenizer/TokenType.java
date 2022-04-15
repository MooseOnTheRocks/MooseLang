package dev.foltz.mooselang.tokenizer;

public enum TokenType {
    T_WHITESPACE,
    T_NEWLINE,

    T_KW_LET,
    T_KW_DEF,
    T_KW_FOR,
    T_KW_IN,
    T_KW_DO,
    T_KW_LAMBDA,

    T_COMMENT,
    T_NAME,
    T_NUMBER,
    T_STRING,
    T_NONE,
    T_ELLIPSES,
    T_FAT_ARROW,

    T_EQUALS,
    T_COMMA,

    T_LPAREN,
    T_RPAREN,
    T_LBRACE,
    T_RBRACE,
    T_LBRACKET,
    T_RBRACKET,
}
