package dev.foltz.mooselang.interpreter;

public record NameBinding<T>(String name, T boundObject) {

}
