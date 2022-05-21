package dev.foltz.mooselang.typing.type.builtin;

import dev.foltz.mooselang.typing.type.Type;
import dev.foltz.mooselang.typing.type.TypeUnknown;

public class BuiltinTypes {
    public static final Type TYPE_UNKNOWN = new TypeUnknown();
    public static final Type TYPE_NONE = new TypeNone();
    public static final Type TYPE_BOOL = new TypeBool();
    public static final Type TYPE_STRING = new TypeString();
    public static final Type TYPE_INT = new TypeInt();
}
