package dev.foltz.mooselang.typing.comp;

import dev.foltz.mooselang.typing.value.TypeValue;

public class CompLambda extends TypeComp {
    public final String paramName;
    public final TypeValue paramType;
    public final TypeComp bodyType;

    public CompLambda(String paramName, TypeValue paramType, TypeComp bodyType) {
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
        return other instanceof CompLambda lambda && lambda.paramType.equals(paramType) && lambda.bodyType.equals(bodyType);
    }
}
