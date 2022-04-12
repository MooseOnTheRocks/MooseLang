package dev.foltz.mooselang.interpreter.runtime;

import java.util.List;

public class RTBuiltinPrint extends RTFunc {
    public RTBuiltinPrint() {
        super(List.of("..."));
    }

    public String printRTObject(RTObject obj) {
        if (obj instanceof RTInt rtInt) {
            return "" + rtInt.value;
        }
        else if (obj instanceof RTNone) {
            return "None";
        }
        else if (obj instanceof RTList rtList) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int i = 0; i < rtList.elements.size(); i++) {
                RTObject elem = rtList.elements.get(i);
                String elemString = printRTObject(elem);
                builder.append(elemString);
                if (i != rtList.elements.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
            return builder.toString();
        }
        else if (obj instanceof RTFunc rtFunc) {
            StringBuilder builder = new StringBuilder();
            builder.append("Function(");
            for (int i = 0; i < rtFunc.paramNames.size(); i++) {
                String paramName = rtFunc.paramNames.get(i);
                builder.append(paramName);
                if (i != rtFunc.paramNames.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            return builder.toString();
        }
        else {
            throw new IllegalStateException("Cannot print object: " + obj);
        }
    }

    @Override
    public RTObject call(List<RTObject> params) {
        if (params.isEmpty()) {
            System.out.println();
        }
        else {
            for (int i = 0; i < params.size(); i++) {
                RTObject param = params.get(i);
                System.out.print(printRTObject(param));
                if (i != params.size() - 1) {
                    System.out.print(" ");
                }
            }
        }
        return RTNone.INSTANCE;
    }
}
