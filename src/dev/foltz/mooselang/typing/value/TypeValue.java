package dev.foltz.mooselang.typing.value;

import dev.foltz.mooselang.typing.TypeBase;

public abstract class TypeValue extends TypeBase {
    public static TypeValue fromString(String typeName) {
        return switch (typeName) {
            case "Number" -> new ValueNumber();
            case "String" -> new ValueString();
            case "Unit" -> new ValueUnit();
            default -> throw new RuntimeException("Unknown type name: " + typeName);
        };
    }
}
