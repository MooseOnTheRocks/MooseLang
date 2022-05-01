package dev.foltz.mooselang.interpreter.rt;

import dev.foltz.mooselang.interpreter.RTPrinter;
import dev.foltz.mooselang.interpreter.RTVisitor;

import java.util.List;

public class RTBuiltinPrint extends RTBuiltinFunc {

    public RTBuiltinPrint() {
        super("print");
    }

    @Override
    public RTObject call(List<RTObject> params) {
        for (int i = 0; i < params.size(); i++) {
            RTObject param = params.get(i);
            System.out.print(RTPrinter.print(param));
            if (i != params.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
        return null;
    }

    @Override
    public <T> T accept(RTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
