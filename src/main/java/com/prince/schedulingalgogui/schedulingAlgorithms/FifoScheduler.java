package com.prince.schedulingalgogui.schedulingAlgorithms;

import java.util.LinkedList;
import java.util.Queue;

public final class FifoScheduler extends Scheduler {

    private final Queue<Object> curretActiveItemsInFrameQueue = new LinkedList<>();
    private final Queue<Integer> indexOfFrameToUse = new LinkedList<>();

    public FifoScheduler(
            int pageFrames, Object[] reference,
            Listenable<Integer> pageFrameIndexListener
                        ) {
        super(reference, pageFrames, pageFrameIndexListener);
        // Since this is FIFO, the order of the frame would be
        // from 0 up to how many page frames we have.
        for (int i = 0; i < pageFrames; i++) {
            indexOfFrameToUse.add(i);
        }
    }

    @Override
    protected void handlePageInsertStatus(Object referenceItem, int referenceItemIndex) {
        if (curretActiveItemsInFrameQueue.contains(referenceItem)) {
            pageHit++;
            isPageHit = true;
            pageResultStatuses[referenceItemIndex] = PageResultStatus.PAGE_HIT;
        } else {
            if (curretActiveItemsInFrameQueue.size() == pageFrames) {
                curretActiveItemsInFrameQueue.remove();
            }
            curretActiveItemsInFrameQueue.add(referenceItem);
            pageFault++;
            pageResultStatuses[referenceItemIndex] = PageResultStatus.PAGE_FAULT;
            isPageHit = false;
        }
    }

    protected void handlePageInsertStatusResult(Object referenceItem, int referenceItemIndex) {
        // If it's not referenceItem hit, we need to remove the first
        // frame from the referenceItemIndex of frame to use and add it to the end of the queue.
        if (!isPageHit) {
            final int indexOfFrameWhichShouldBeUsed = indexOfFrameToUse.remove();
            for (int i = 0; i < pageFrames; i++) {
                // If the current frame is the frame which should be used, we need to
                // insert the referenceItem to the current frame.
                // Else just copy the previous data from the current frame.
                if (i == indexOfFrameWhichShouldBeUsed) {
                    result[i][referenceItemIndex] = referenceItem;
                } else if (referenceItemIndex - 1 >= 0) {
                    result[i][referenceItemIndex] = result[i][referenceItemIndex - 1];
                }
            }
            indexOfFrameToUse.add(indexOfFrameWhichShouldBeUsed);
        } else if (referenceItemIndex - 1 >= 0) {
            // If it's a referenceItem hit, we do nothing about the referenceItemIndex of the frame to use
            // and just copy the previous data from the current frame to the
            // current data of the frame.
            for (int i = 0; i < pageFrames; i++) {
                result[i][referenceItemIndex] = result[i][referenceItemIndex - 1];
            }
        }
    }
}
