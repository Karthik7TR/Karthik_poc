package com.thomsonreuters.uscl.ereader.img;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class ImageGathererRequest implements Callable<Boolean> {
    private static Logger LOG = LogManager.getLogger(ImageGathererRequest.class);

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
            LOG.error("", e);
            return false;
        }
    }

    protected abstract void doRequest() throws Exception;
}
