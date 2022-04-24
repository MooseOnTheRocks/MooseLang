package dev.foltz.mooselang.parser.ast.deconstructors;

import dev.foltz.mooselang.interpreter.Scope;
import dev.foltz.mooselang.interpreter.runtime.RTObject;
import dev.foltz.mooselang.parser.ast.ASTNode;

public abstract class ASTDeconstructor extends ASTNode {
    public abstract boolean matches(RTObject rtObj);
    public abstract RTObject deconstruct(RTObject rtObj, Scope scope);
}
