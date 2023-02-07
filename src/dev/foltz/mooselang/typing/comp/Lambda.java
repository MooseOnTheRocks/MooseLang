package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.StringType;
import dev.foltz.mooselang.typing.value.ValueType;

public class Lambda extends CompType {
    public final String paramName;
    public final ValueType paramType;
    public final CompType bodyType;

    public Lambda(String paramName, ValueType paramType, CompType bodyType) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.bodyType = bodyType;
    }

    @Override
    public String toString() {
        return "Lambda(" + paramName + ", " + paramType + ", " + bodyType + ")";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Lambda lambda && lambda.paramType.equals(paramType) && lambda.bodyType.equals(bodyType);
    }
}
