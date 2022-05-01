package dev.foltz.mooselang.interpreter.rt;

import dev.foltz.mooselang.interpreter.RTPrinter;
import dev.foltz.mooselang.interpreter.RTVisitor;

public abstract class RTObject {
    public abstract <T> T accept(RTVisitor<T> visitor);
}
