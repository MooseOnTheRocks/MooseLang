package dev.foltz.mooselang;

import dev.foltz.mooselang.ast.ASTNode;
import dev.foltz.mooselang.parser.Combinators;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.SourceDesc;

import static dev.foltz.mooselang.parser.Parsers.*;

public class MooseLang {
    public static void main(String[] args) {
//        var source = SourceDesc.fromString("test", "let axe = 200");
        var source = SourceDesc.fromFile("tests", "test.msl");

        var toplevel = Combinators.any(
            anyws,
            comment,
            stmtLet,
            stmtDef
        );

        var parser = Combinators.many(toplevel);

        var res = Parsers.parse(parser, source);
        if (res.isError) {
            System.out.println("Error");
            System.out.println(res.source.name());
            System.out.println(res.index);
            System.out.println("-- Remainder");
            System.out.println(res.rem());
        }
        else {
            System.out.println("Success");
            System.out.println(res.source.name());
            System.out.println(res.index);
            System.out.println(res.result);
            System.out.println("-- AST Nodes");
            var asts = res.result.stream().filter(e -> e instanceof ASTNode).toList();
            asts.forEach(System.out::println);
            System.out.println("-- Remainder");
            System.out.println(res.rem());
        }
    }
}
