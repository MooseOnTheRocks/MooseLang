package dev.foltz.mooselang.interpreter;

import dev.foltz.mooselang.interpreter.rt.RTObject;

import java.util.List;

/*
 * A scope defines a mapping of names to values.
 * Scope semantics will differ based on implementation.
 */
public interface IScope {
    List<NameBinding> bindings();
    void bind(String name, RTObject obj);
    boolean contains(String name);
    NameBinding find(String name);
}
