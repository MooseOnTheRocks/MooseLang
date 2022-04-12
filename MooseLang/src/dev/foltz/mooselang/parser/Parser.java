package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtBind;
import dev.foltz.mooselang.parser.ast.statements.ASTStmt;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtExpr;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.tokenizer.TokenType.*;

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

    public ASTExprName parseName() {
        String name = consume(T_NAME).value;
        return new ASTExprName(name);
    }

    public ASTExprInt parseNumber() {
        int value = Integer.parseInt(consume(T_NUMBER).value);
        return new ASTExprInt(value);
    }

    public ASTExprList parseList() {
        consume(T_LBRACKET);
        // Empty list
        if (expect(T_RBRACKET)) {
            consume(T_RBRACKET);
            return new ASTExprList(List.of());
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
        return new ASTExprList(elems);
    }

    public ASTExprCall parseCall() {
        ASTExprName name = parseName();
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
        return new ASTExprCall(name, params);
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

    public ASTStmtBind parseBind() {
        ASTExprName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        return new ASTStmtBind(name, expr);
    }

    public ASTStmt parseStmt() {
        if (expect(T_NAME) && expect(T_EQUALS, 1)) {
            return parseBind();
        }
        else {
            return new ASTStmtExpr(parseExpr());
        }
    }

    public List<ASTStmt> parse() {
        List<ASTStmt> nodes = new ArrayList<>();
        while (!isEmpty()) {
            nodes.add(parseStmt().evalStmt());
        }
        return nodes;
    }
}
