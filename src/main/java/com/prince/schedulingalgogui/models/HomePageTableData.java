package com.prince.schedulingalgogui.models;

import com.prince.schedulingalgogui.schedulingAlgorithms.SchedulerResult;
import lombok.Getter;

@Getter
public final class HomePageTableData {
    private final int currentFrame;
    private final int currentIndex;
    private final int frameCount;
    private final boolean isCurrentColumnFrameCount;
    private final SchedulerResult schedulerResult;

    public HomePageTableData(int frameCount, int currentFrame, int currentIndex, SchedulerResult schedulerResult , boolean isCurrentColumnFrameCount) {
        this.frameCount = frameCount;
        this.currentFrame = currentFrame;
        this.currentIndex = currentIndex;
        this.schedulerResult = schedulerResult;
        this.isCurrentColumnFrameCount = isCurrentColumnFrameCount;
    }
}
