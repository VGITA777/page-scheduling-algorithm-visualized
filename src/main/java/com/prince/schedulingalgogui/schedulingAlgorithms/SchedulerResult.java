package com.prince.schedulingalgogui.schedulingAlgorithms;

import lombok.Getter;

import java.util.Arrays;

@Getter
public final class SchedulerResult {

    private final int pageHit;
    private final int pageFault;
    private final Object[][] result;
    private final PageResultStatus[] pageResultStatuses;
    private final Object[] stringReference;

    public SchedulerResult(
            int pageHit, int pageFault, Object[][] result,
            PageResultStatus[] pageResultStatuses, Object[] stringReference
                          ) {
        this.pageHit = pageHit;
        this.pageFault = pageFault;
        this.result = result;
        this.pageResultStatuses = pageResultStatuses;
        this.stringReference = stringReference;
    }

    @Override
    public String toString() {
        return "SchedulerResult{" +
                "pageHit=" + pageHit +
                ", pageFault=" + pageFault +
                ", result=" + Arrays.deepToString(result) +
                ", pageResultStatuses=" + ((pageResultStatuses != null) ? Arrays.stream(pageResultStatuses)
                                                                                .map(Enum::toString).toList() : "[]") +
                '}';
    }
}
