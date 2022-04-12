package dev.foltz.mooselang.parse;

import dev.foltz.mooselang.parse.expressions.*;
import dev.foltz.mooselang.parse.statements.ASTBind;
import dev.foltz.mooselang.parse.statements.ASTStmt;
import dev.foltz.mooselang.parse.statements.ASTStmtExpr;
import dev.foltz.mooselang.token.Token;
import dev.foltz.mooselang.token.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.token.TokenType.*;

/*
 * program = [expr]
 * expr = name '=' expr
 *      | name
 *      | number
 */

public class Parser {
    private List<Token> remainder;

    public Parser() {
        this.remainder = new ArrayList<>();
    }

    public boolean isEmpty() {
        return remainder.isEmpty();
    }

    public Parser feed(Token token) {
        remainder.add(token);
        return this;
    }

    private boolean expect(TokenType type) {
        return expect(type, 0);
    }

    private boolean expect(TokenType type, int lookahead) {
        return peek(lookahead).type == type;
    }

    private Token peek() {
        return peek(0);
    }

    private Token peek(int lookahead) {
        return remainder.get(lookahead);
    }

    private Token consume(TokenType type) {
        TokenType peekType = peek().type;
        if (peekType != type) {
            throw new IllegalStateException("Expected token of type " + type.name() + ", but found type " + peekType.name());
        }
        return remainder.remove(0);
    }

    public ASTName parseName() {
        String name = consume(T_NAME).value;
        return new ASTName(name);
    }

    public ASTInt parseNumber() {
        int value = Integer.parseInt(consume(T_NUMBER).value);
        return new ASTInt(value);
    }

    public ASTList parseList() {
        consume(T_LBRACKET);
        // Empty list
        if (expect(T_RBRACKET)) {
            consume(T_RBRACKET);
            return new ASTList(List.of());
        }

        List<ASTExpr> elems = new ArrayList<>();
        // First item
        elems.add(parseExpr());
        // Any other items
        while (expect(T_COMMA)) {
            consume(T_COMMA);
            elems.add(parseExpr());
        }
        consume(T_RBRACKET);
        return new ASTList(elems);
    }

    public ASTCall parseCall() {
        ASTName name = parseName();
        consume(T_LPAREN);
        List<ASTExpr> params = new ArrayList<>();
        // First item
        params.add(parseExpr());
        // Any other items
        while (expect(T_COMMA)) {
            consume(T_COMMA);
            params.add(parseExpr());
        }
        consume(T_RPAREN);
        return new ASTCall(name, params);
    }

    public ASTExpr parseExpr() {
        if (expect(T_NAME)) {
            if (expect(T_LPAREN, 1)) {
                return parseCall();
            }
            else {
                return parseName();
            }
        }
        else if (expect(T_NUMBER)) {
            return parseNumber();
        }
        else if (expect(T_LBRACKET)) {
            return parseList();
        }
        throw new IllegalStateException("Failed to parse expression: " + peek());
    }

    public ASTBind parseBind() {
        ASTName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        return new ASTBind(name, expr);
    }

    public ASTStmt parseStmt() {
        if (expect(T_NAME) && expect(T_EQUALS, 1)) {
            return parseBind();
        }
        else {
            return new ASTStmtExpr(parseExpr());
        }
//        throw new IllegalStateException("Failed to parse statement: " + peek());
    }

    public List<ASTStmt> parse() {
        List<ASTStmt> nodes = new ArrayList<>();
        while (!isEmpty()) {
            nodes.add(parseStmt().evalStmt());
        }
        return nodes;
    }
}
