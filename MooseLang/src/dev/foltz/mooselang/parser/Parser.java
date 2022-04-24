package dev.foltz.mooselang.parser;

import dev.foltz.mooselang.parser.ast.deconstructors.*;
import dev.foltz.mooselang.parser.ast.expressions.*;
import dev.foltz.mooselang.parser.ast.expressions.literals.*;
import dev.foltz.mooselang.parser.ast.expressions.ASTExprName;
import dev.foltz.mooselang.parser.ast.statements.*;
import dev.foltz.mooselang.tokenizer.Token;
import dev.foltz.mooselang.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static dev.foltz.mooselang.tokenizer.TokenType.*;


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
        return !remainder.isEmpty() && expect(type, 0);
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

    public ASTExprString parseChar() {
        return new ASTExprString(consume(T_CHAR).value);
    }

    public ASTExprLambda parseLambda() {
        consume(T_KW_LAMBDA);
        List<ASTDeconstructor> paramDtors = new ArrayList<>();
        if (expect(T_LPAREN)) {
            consume(T_LPAREN);
            if (expect(T_RPAREN)) {
               consume(T_RPAREN);
            }
            else {
                paramDtors.add(parseDeconstructor());
                while (expect(T_COMMA)) {
                    consume(T_COMMA);
                    paramDtors.add(parseDeconstructor());
                }
                consume(T_RPAREN);
            }
        }
        else if (!expect(T_FAT_ARROW)) {
            paramDtors.add(parseDeconstructor());
            while (expect(T_COMMA)) {
                consume(T_COMMA);
                paramDtors.add(parseDeconstructor());
            }
        }
        consume(T_FAT_ARROW);
        ASTStmt body = parseStmt();
        return new ASTExprLambda(paramDtors, body);
    }

    public ASTExprBool parseBool() {
        if (expect(T_TRUE)) {
            consume(T_TRUE);
            return new ASTExprBool(true);
        }
        else {
            consume(T_FALSE);
            return new ASTExprBool(false);
        }
    }

    public ASTExprIfThenElse parseIfThenElse() {
        consume(T_KW_IF);
        ASTExpr exprCond = parseExpr();
        consume(T_KW_THEN);
        ASTExpr exprTrue = parseExpr();
        consume(T_KW_ELSE);
        ASTExpr exprFalse = parseExpr();
        return new ASTExprIfThenElse(exprCond, exprTrue, exprFalse);
    }

    public ASTExpr parseExpr() {
        if (expect(T_NAME)) {
            if (expect(T_LPAREN, 1)) {
                return parseCall();
            }
            else if (expect(T_EQUALS, 1)) {
                return parseAssign();
            }
            else {
                return parseName();
            }
        }
        else if (expect(T_MINUS)) {
            consume(T_MINUS);
            return new ASTExprNegate(parseExpr());
        }
        else if (expect(T_NUMBER)) {
            return parseNumber();
        }
        else if (expect(T_STRING)) {
            return parseString();
        }
        else if (expect(T_CHAR)) {
            return parseChar();
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
        else if (expect(T_KW_LAMBDA)) {
            return parseLambda();
        }
        else if (expect(T_KW_LET)) {
            return parseLetIn();
        }
        else if (expect(T_KW_FOR)) {
            return parseForInThenElse();
        }
        else if (expect(T_TRUE) || expect(T_FALSE)) {
            return parseBool();
        }
        else if (expect(T_KW_IF)) {
            return parseIfThenElse();
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
        else if (expect(T_CHAR)) {
            return new ASTDeconChar(parseChar());
        }
        else if (expect(T_LBRACKET)) {
            consume(T_LBRACKET);
            List<ASTDeconstructor> decons = new ArrayList<>();
            if (!expect(T_RBRACKET)) {
                decons.add(parseDeconstructor());
                while (expect(T_COMMA)) {
                    consume(T_COMMA);
                    decons.add(parseDeconstructor());
                }
            }
            consume(T_RBRACKET);
            return new ASTDeconList(decons);
        }
        throw new IllegalStateException("Failed to parse deconstructor: " + peek());
    }

    public ASTStmtFuncDef parseFuncDef() {
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
        if (expect(T_EQUALS)) {
            consume(T_EQUALS);
        }
        ASTStmt body = parseStmt();
        ASTStmtFuncDef funcDef = new ASTStmtFuncDef(name, paramDtors, body);
        return funcDef;
    }

    public Object parseForIn_Do_ThenElse() {
        consume(T_KW_FOR);
        ASTDeconstructor decon = parseDeconstructor();
        consume(T_KW_IN);
        ASTExpr listExpr = new ASTExprCall(new ASTExprName("iter"), List.of(parseExpr()));
        if (expect(T_KW_THEN)) {
            consume(T_KW_THEN);
            ASTExpr bodyLoop = parseExpr();
            consume(T_KW_ELSE);
            ASTExpr bodyElse = parseExpr();
            return new ASTExprForInThenElse(decon, listExpr, bodyLoop, bodyElse);
        }
        else {
            consume(T_KW_DO);
            ASTExpr bodyLoop = parseExpr();
            return new ASTStmtForInDo(decon, listExpr, bodyLoop);
        }
    }

    public ASTExprForInThenElse parseForInThenElse() {
        consume(T_KW_FOR);
        ASTDeconstructor decon = parseDeconstructor();
        consume(T_KW_IN);
        ASTExpr listExpr = new ASTExprCall(new ASTExprName("iter"), List.of(parseExpr()));
        consume(T_KW_THEN);
        ASTExpr bodyLoop = parseExpr();
        consume(T_KW_ELSE);
        ASTExpr bodyElse = parseExpr();
        return new ASTExprForInThenElse(decon, listExpr, bodyLoop, bodyElse);
    }

    public ASTStmtForInDo parseForInDo() {
        consume(T_KW_FOR);
        ASTDeconstructor decon = parseDeconstructor();
        consume(T_KW_IN);
        ASTExpr listExpr = parseExpr();
        consume(T_KW_DO);
        ASTExpr body = parseExpr();
        return new ASTStmtForInDo(decon, listExpr, body);
    }

    public Object parseLetMaybeIn() {
        consume(T_KW_LET);
        ASTExprName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        if (expect(T_KW_IN)) {
            consume(T_KW_IN);
            ASTExpr body = parseExpr();
            return new ASTExprLetIn(name, expr, body);
        }
        else {
            return new ASTStmtLet(name, expr);
        }
    }

    public ASTExprLetIn parseLetIn() {
        consume(T_KW_LET);
        ASTExprName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        if (expect(T_KW_IN)) {
            consume(T_KW_IN);
        }
        ASTExpr body = parseExpr();
        return new ASTExprLetIn(name, expr, body);
    }

//    public ASTStmtLet parseLet() {
//        consume(T_KW_LET);
//        ASTExprName name = parseName();
//        consume(T_EQUALS);
//        ASTExpr expr = parseExpr();
//        return new ASTStmtLet(name, expr);
//    }

    public ASTExprAssign parseAssign() {
        ASTExprName name = parseName();
        consume(T_EQUALS);
        ASTExpr expr = parseExpr();
        return new ASTExprAssign(name, expr);
    }

//    public ASTStmtIfDo parseIfDo() {
//        consume(T_KW_IF);
//        ASTExpr exprCond = parseExpr();
//        consume(T_KW_DO);
//        ASTExpr exprTrue = parseExpr();
//        return new ASTStmtIfDo(exprCond, exprTrue);
//    }

    public Object parseIf_Do_ThenElse() {
        consume(T_KW_IF);
        ASTExpr exprCond = parseExpr();
        if (expect(T_KW_DO)) {
            consume(T_KW_DO);
            ASTExpr exprTrue = parseExpr();
            return new ASTStmtIfDo(exprCond, exprTrue);
        }
        else if (expect(T_KW_THEN)) {
            consume(T_KW_THEN);
            ASTExpr exprTrue = parseExpr();
            consume(T_KW_ELSE);
            ASTExpr exprFalse = parseExpr();
            return new ASTExprIfThenElse(exprCond, exprTrue, exprFalse);
        }

        throw new IllegalStateException("Expected T_KW_DO or T_KW_THEN while parsing If, received: " + peek());
    }

    public ASTStmt parseStmt() {
        if (expect(T_KW_DEF)) {
            return new ASTStmtExpr(parseFuncDef());
        }
        else if (expect(T_KW_LET)) {
            Object letMaybeIn = parseLetMaybeIn();
            if (letMaybeIn instanceof ASTStmtLet stmtLet) {
                return stmtLet;
            }
            else if (letMaybeIn instanceof ASTExprLetIn exprLetIn) {
                return new ASTStmtExpr(exprLetIn);
            }
        }
        else if (expect(T_KW_IF)) {
            Object ifMaybeElse = parseIf_Do_ThenElse();
            if (ifMaybeElse instanceof ASTStmtIfDo stmtIfDo) {
                return stmtIfDo;
            }
            else if (ifMaybeElse instanceof ASTExprIfThenElse exprIfThenElse) {
                return new ASTStmtExpr(exprIfThenElse);
            }
        }
        else if (expect(T_KW_FOR)) {
            Object forIn = parseForIn_Do_ThenElse();
            if (forIn instanceof ASTStmtForInDo stmtForInDo) {
                return stmtForInDo;
            }
            else if (forIn instanceof ASTExprForInThenElse exprForInThenElse) {
                return new ASTStmtExpr(exprForInThenElse);
            }
        }
        else {
            return new ASTStmtExpr(parseExpr());
        }

        throw new IllegalStateException("Failed to parse statement: " + peek());
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
