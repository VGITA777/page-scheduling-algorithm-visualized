package com.prince.schedulingalgogui.schedulingAlgorithms;

import java.util.Optional;

public interface IScheduler {
    SchedulerResult start();

    int getPageFrames();

    Object[] getReference();

    Optional<SchedulerResult> getSchedulerResult();
}
