package dev.foltz.mooselang;

import dev.foltz.mooselang.parser.Combinators;
import dev.foltz.mooselang.parser.Parsers;
import dev.foltz.mooselang.parser.SourceDesc;

import static dev.foltz.mooselang.parser.Combinators.defaulted;
import static dev.foltz.mooselang.parser.Parsers.*;

public class MooseLang {
    public static void main(String[] args) {
        var source = SourceDesc.fromString("test", "-- hello world\n    \n  \n\n   let axe = 200");

        var parser = Combinators.many1(Combinators.any(wsnl, comment, Combinators.all(match("let"), anyws, name, anyws, match("="), anyws, number)));

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
            System.out.println("-- Remainder");
            System.out.println(res.rem());
        }
    }
}
