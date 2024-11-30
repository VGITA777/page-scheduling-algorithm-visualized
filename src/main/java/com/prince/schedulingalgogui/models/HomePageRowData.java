package com.prince.schedulingalgogui.models;

import com.prince.schedulingalgogui.schedulingAlgorithms.SchedulerResult;
import lombok.Getter;

@Getter
public class HomePageRowData {
    private final int framePosition;
    private final SchedulerResult schedulerResult;

    public HomePageRowData(int framePosition, SchedulerResult schedulerResult) {
        this.framePosition = framePosition;
        this.schedulerResult = schedulerResult;
    }
}
