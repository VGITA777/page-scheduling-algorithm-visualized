package com.prince.schedulingalgogui.schedulingAlgorithms;

import java.util.Arrays;

public final class SchedulerResult {
    private final int pageHit;
    private final int pageFault;
    private final Object[][] result;
    private final PageResultStatus[] pageResultStatuses;

    public SchedulerResult(int pageHit, int pageFault, Object[][] result, PageResultStatus[] pageResultStatuses) {
        this.pageHit = pageHit;
        this.pageFault = pageFault;
        this.result = result;
        this.pageResultStatuses = pageResultStatuses;
    }

    @Override
    public String toString() {
        return "SchedulerResult{" +
                "pageHit=" + pageHit +
                ", pageFault=" + pageFault +
                ", result=" + Arrays.deepToString(result) +
                ", pageResultStatuses=" + ((pageResultStatuses != null) ? Arrays.stream(pageResultStatuses).map(Enum::toString).toList() : "[]") +
                '}';
    }

    public int getPageHit() {
        return pageHit;
    }

    public int getPageFault() {
        return pageFault;
    }

    public Object[][] getResult() {
        return result;
    }

    public PageResultStatus[] getPageResults() {
        return pageResultStatuses;
    }
}
