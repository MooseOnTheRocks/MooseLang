package dev.foltz.mooselang;

//import dev.foltz.mooselang.parse.Parser;
import dev.foltz.mooselang.parse.statements.ASTStmt;
import dev.foltz.mooselang.parse.Parser;
import dev.foltz.mooselang.token.Token;
import dev.foltz.mooselang.token.TokenType;
import dev.foltz.mooselang.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, MooseLang!");
        String program = """
                numbers = [1, 2, 3, 4, 5]
                print(numbers)
                """;

        Tokenizer tokenizer = new Tokenizer();
        tokenizer.feed(program);
        List<Token> tokens = new ArrayList<>();
        while (!tokenizer.isEmpty()) {
            Token token = tokenizer.nextToken();
            if (token.type == TokenType.T_WHITESPACE || token.type == TokenType.T_NEWLINE) {
                continue;
            }
            tokens.add(token);
        }

        Parser parser = new Parser();
        tokens.forEach(parser::feed);
        List<ASTStmt> ast = parser.parse();
        System.out.println(ast);
    }
}
