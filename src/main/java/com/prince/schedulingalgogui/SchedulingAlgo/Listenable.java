package com.prince.schedulingalgogui.SchedulingAlgo;

@FunctionalInterface
public interface Listenable<T> {
    void onChange(T value);
}
