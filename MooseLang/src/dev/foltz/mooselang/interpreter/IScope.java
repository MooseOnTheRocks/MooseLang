package dev.foltz.mooselang.interpreter;

/*
 * A scope defines a mapping of names to values.
 * Scope semantics will differ based on implementation.
 */
public interface IScope<T> {
    void bind(String name, T obj);
    boolean contains(String name);
    NameBinding<T> find(String name);
}
