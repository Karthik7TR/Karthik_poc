package com.thomsonreuters.uscl.ereader.img;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public abstract class ImageGathererRequest implements Callable<Boolean> {

    private CountDownLatch countDownLatch;

    public ImageGathererRequest(final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            countDownLatch.await();
            doRequest();
            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    protected abstract void doRequest() throws Exception;
}
