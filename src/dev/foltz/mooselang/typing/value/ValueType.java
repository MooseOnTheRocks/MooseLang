package dev.foltz.mooselang.typing.value;

import dev.foltz.mooselang.typing.BaseType;

public abstract class ValueType extends BaseType {
    public static ValueType fromString(String typeName) {
        return switch (typeName) {
            case "Number" -> new NumberType();
            case "String" -> new StringType();
            case "Unit" -> new Unit();
            default -> throw new RuntimeException("Unknown type name: " + typeName);
        };
    }
}
