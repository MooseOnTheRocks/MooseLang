package dev.foltz.mooselang.ir;

public abstract class IRNode {
    public abstract <T> T apply(IRVisitor<T> visitor);
}
