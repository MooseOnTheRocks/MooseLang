package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprInt;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprList;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprNone;
import dev.foltz.mooselang.parser.ast.expressions.literals.ASTExprString;
import dev.foltz.mooselang.parser.ast.statements.ASTStmtAssign;
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
        if (!expect(T_RPAREN)) {
            // First item
            params.add(parseExpr());
            // Any other items
            while (expect(T_COMMA)) {
                consume(T_COMMA);
                params.add(parseExpr());
            }
        }
        consume(T_RPAREN);
        return new ASTExprCall(name, params);
    }

    public ASTExprString parseString() {
        return new ASTExprString(consume(T_STRING).value);
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
        else if (expect(T_STRING)) {
            return parseString();
        }
        else if (expect(T_LBRACKET)) {
            return parseList();
        }
        else if (expect(T_LBRACE)) {
            return parseBlock();
        }
        else if (expect(T_NONE)) {
            consume(T_NONE);
            return new ASTExprNone();
        }
        throw new IllegalStateException("Failed to parse expression: " + peek());
    }

    public ASTDeconstructor parseDeconstructor() {
        if (expect(T_NAME)) {
            return new ASTDeconName(parseName());
        }
        else if (expect(T_NUMBER)) {
            return new ASTDeconInt(parseNumber());
        }
        else if (expect(T_STRING)) {
            return new ASTDeconString(parseString());
        }
        else if (expect(T_LBRACKET)) {
            consume(T_LBRACKET);
            List<ASTDeconstructor> decons = new ArrayList<>();
            while (!expect(T_RBRACKET)) {
                decons.add(parseDeconstructor());
            }
            consume(T_RBRACKET);
            return new ASTDeconList(decons);
        }
        throw new IllegalStateException("Failed to parse deconstructor: " + peek());
    }

    public ASTExprFuncDef parseFuncDef() {
        consume(T_KW_DEF);
        ASTExprName name = parseName();
        consume(T_LPAREN);
        List<ASTDeconstructor> paramDtors = new ArrayList<>();
        if (!expect(T_RPAREN)) {
            // First item
            paramDtors.add(parseDeconstructor());
            // Any other items
            while (expect(T_COMMA)) {
                consume(T_COMMA);
                paramDtors.add(parseDeconstructor());
            }
        }
        consume(T_RPAREN);
        consume(T_EQUALS);
        ASTExpr body = parseExpr();
        ASTExprFuncDef funcDef = new ASTExprFuncDef(name, paramDtors, body);
        return funcDef;
    }

    public ASTStmtAssign parseBind() {
        consume(T_KW_LET);
        ASTExprName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        return new ASTStmtAssign(name, expr);
    }

    public ASTStmt parseStmt() {
        if (expect(T_KW_DEF)) {
            return new ASTStmtExpr(parseFuncDef());
        }
        else if (expect(T_KW_LET)) {
            return parseBind();
        }
        else {
            return new ASTStmtExpr(parseExpr());
        }
    }

    public ASTExprBlock parseBlock() {
        List<ASTStmt> stmts = new ArrayList<>();
        consume(T_LBRACE);
        while (!expect(T_RBRACE)) {
            stmts.add(parseStmt());
        }
        consume(T_RBRACE);
        return new ASTExprBlock(stmts);
    }

    public List<ASTStmt> parse() {
        List<ASTStmt> nodes = new ArrayList<>();
        while (!isEmpty()) {
            nodes.add(parseStmt());
        }
        return nodes;
    }
}