package dev.foltz.mooselang.interpreter.runtime;

import java.util.stream.Collectors;

public class RTFuncPrint extends RTFunc {
    public RTFuncPrint() {
        super("print");
    }

    public static String print(RTObject obj) {
        StringBuilder sb = new StringBuilder();
        if (obj instanceof RTNone) {
            sb.append("None");
        }
        else if (obj instanceof RTInt rtInt) {
            sb.append(rtInt.value);
        }
        else if (obj instanceof RTString rtString) {
            sb.append(rtString.value);
        }
        else if (obj instanceof RTList rtList) {
            sb.append("[");
            sb.append(rtList.elems.stream()
                    .map(RTFuncPrint::print)
                    .collect(Collectors.joining(", "))
            );
            sb.append("]");
        }
        else {
            throw new UnsupportedOperationException("print does not support object: " + obj);
        }
        return sb.toString();
    }
}
