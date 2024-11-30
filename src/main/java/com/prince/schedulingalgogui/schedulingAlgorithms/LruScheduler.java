package com.prince.schedulingalgogui.schedulingAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LruScheduler extends Scheduler {
    private final List<Object> currentActiveItemsInFrame = new ArrayList<>();
    private final Map<Integer, Integer> frameAgeMap = new HashMap<>();

    public LruScheduler(int pageFrames, Object[] reference, Listenable<Integer> pageFrameIndexListener) {
        super(reference, pageFrames, pageFrameIndexListener);
        for (int i = 0; i < pageFrames; i++) {
            frameAgeMap.put(i, 0);
        }
    }

    @Override
    protected void handlePageInsertStatus(Object referenceItem, int referenceItemIndex) {
        // If the current reference item is already in the current active items in frame,
        // We would need to put it to the end of the list because it's been used.
        if (currentActiveItemsInFrame.contains(referenceItem)) {
            // Since the reference is in the list, we need to remove it from the list
            // and add it to the end of the list because it's the most recently used.
            currentActiveItemsInFrame.remove(referenceItem);
            currentActiveItemsInFrame.addLast(referenceItem);

            isPageHit = true;
            pageHit += 1;
            pageResultStatuses[referenceItemIndex] = PageResultStatus.PAGE_HIT;
        } else {
            // If the current active items in frame is equal to the referenceItem frames,
            // It means that we need to remove the item in the list.
            // that has the least activity and add the new reference item.
            if (currentActiveItemsInFrame.size() == pageFrames) {
                currentActiveItemsInFrame.removeFirst();
            }
            currentActiveItemsInFrame.addLast(referenceItem);

            isPageHit = false;
            pageFault += 1;
            pageResultStatuses[referenceItemIndex] = PageResultStatus.PAGE_FAULT;
        }
    }

    @Override
    protected void handlePageInsertStatusResult(Object referenceItem, int referenceItemIndex) {
        // Age all frames
        ageFrames();

        if (!isPageHit) {
            final int indexOfFrameWhichShouldBeUsed = getLeastRecentlyUsedFrameKey();
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
            resetFrameAge(indexOfFrameWhichShouldBeUsed);
        } else if (referenceItemIndex - 1 >= 0) {
            // If it's a referenceItem hit, we do nothing about the referenceItemIndex of the frame to use
            // and just copy the previous data from the current frame to the
            // current data of the frame. But we need to reset the age of the current frame.
            for (int i = 0; i < pageFrames; i++) {
                result[i][referenceItemIndex] = result[i][referenceItemIndex - 1];
                // Check if the last item from the current frame has the same value as the referenceItem
                // If it is then we reset the age of the current frame because it was used.
                if (result[i][referenceItemIndex] == referenceItem || result[i][referenceItemIndex].equals(referenceItem)) {
                    resetFrameAge(i);
                }
            }
        }
    }

    private void ageFrames() {
        frameAgeMap.replaceAll((_, v) -> v + 1);
    }

    private void resetFrameAge(int key) {
        frameAgeMap.put(key, 0);
    }

    private int getLeastRecentlyUsedFrameKey() {
        int age = -1;
        int key = -1;

        for (int k : frameAgeMap.keySet()) {
            if (frameAgeMap.get(k) > age) {
                age = frameAgeMap.get(k);
                key = k;
            }
        }

        return key;
    }
}
