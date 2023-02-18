package dev.foltz.mooselang.ir.nodes.type.tag;

import dev.foltz.mooselang.ir.nodes.type.IRType;

public abstract class IRTypeTag extends IRType {
    @Override
    public abstract boolean equals(Object other);
}
