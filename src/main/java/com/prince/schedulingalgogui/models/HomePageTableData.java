package com.prince.schedulingalgogui.models;

import com.prince.schedulingalgogui.schedulingAlgorithms.SchedulerResult;
import lombok.Getter;

@Getter
public class HomePageTableData {
    private final int frameCount;
    private final SchedulerResult schedulerResult;

    public HomePageTableData(int frameCount, SchedulerResult schedulerResult) {
        this.frameCount = frameCount;
        this.schedulerResult = schedulerResult;
    }
}
