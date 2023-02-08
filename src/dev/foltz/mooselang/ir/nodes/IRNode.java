package dev.foltz.mooselang.ir.nodes;

import dev.foltz.mooselang.ir.VisitorIR;

public abstract class IRNode {
    public abstract <T> T apply(VisitorIR<T> visitor);
}
