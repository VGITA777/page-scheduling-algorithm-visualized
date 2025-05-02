package com.prince.schedulingalgogui.schedulingAlgorithms;

@FunctionalInterface
public interface ISchedulerFactory {

    Scheduler createScheduler(int pageFrames, Object[] reference);
}
