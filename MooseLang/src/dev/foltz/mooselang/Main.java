package dev.foltz.mooselang;

//import dev.foltz.mooselang.parse.Parser;
import dev.foltz.mooselang.token.Token;
import dev.foltz.mooselang.token.TokenType;
import dev.foltz.mooselang.token.Tokenizer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        String p1 = """
                123numbers123 = (1, 2, 3, 4)
                """;
        String p2 = """
                for n in numbers {
                    print(n)
                }""";

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.feed(p1).feed(p2);
        while (!tokenizer.isEmpty()) {
            Token token = tokenizer.nextToken();
            if (token.type == TokenType.T_WHITESPACE || token.type == TokenType.T_NEWLINE) {
                continue;
            }
            System.out.println(token);
        }
    }
}
