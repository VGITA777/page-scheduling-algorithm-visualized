package com.prince.schedulingalgogui.SchedulingAlgo;

import java.util.Optional;

public abstract class Scheduler implements IScheduler {
    protected final int pageFrames;
    protected final Object[] reference;
    protected final Object[][] result;
    protected final PageResultStatus[] pageResultStatuses;
    protected int pageHit = 0;
    protected int pageFault = 0;
    protected int currentPageFrameIndex = 0;
    protected boolean isPageHit = false;
    protected SchedulerResult schedulerResult;
    protected Listenable<Integer> pageFrameIndexListener;

    protected Scheduler(Object[] reference, int pageFrames, Listenable<Integer> pageFrameIndexListener) {
        this.reference = reference;
        this.pageFrames = pageFrames;
        this.result = new Object[pageFrames][reference.length];
        this.pageResultStatuses = new PageResultStatus[reference.length];
        this.pageFrameIndexListener = pageFrameIndexListener;
    }

    @Override
    public SchedulerResult start() {
        // Access each item from the reference array.
        for (int i = 0; i < reference.length; i++) {
            final Object page = reference[i];
            handlePageInsertStatus(page, i);
            handlePageInsertStatusResult(page, i);
            incrementCurrentPageFrameIndex();
        }

        schedulerResult = new SchedulerResult(pageHit, pageFault, result, pageResultStatuses);
        return schedulerResult;
    }

    @Override
    public int getPageFrames() {
        return pageFrames;
    }

    @Override
    public Object[] getReference() {
        return reference;
    }

    @Override
    public Optional<SchedulerResult> getSchedulerResult() {
        if (schedulerResult == null) return Optional.empty();
        return Optional.of(new SchedulerResult(pageHit, pageFault, result, pageResultStatuses));
    }

    protected abstract void handlePageInsertStatus(Object referenceItem, int referenceItemIndex);

    protected abstract void handlePageInsertStatusResult(Object referenceItem, int referenceItemIndex);

    private void incrementCurrentPageFrameIndex() {
        if (currentPageFrameIndex + 1 >= pageFrames) {
            currentPageFrameIndex = 0;
        } else {
            currentPageFrameIndex += 1;
        }

        if (pageFrameIndexListener != null) {
            pageFrameIndexListener.onChange(currentPageFrameIndex);
        }
    }
}
