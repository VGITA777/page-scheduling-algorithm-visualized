package com.prince.schedulingalgogui.schedulingAlgorithms;

@FunctionalInterface
public interface Listenable<T> {

    void onChange(T value);
}
