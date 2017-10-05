package com.thomsonreuters.uscl.ereader.gather.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class NovusUtility {
    /**
     * Retry count. default value 3
     */
    private String docRetryCount;
    private String tocRetryCount;
    private String nortRetryCount;
    private String imgRetryCount;

    /**
    * Flag to check if we want to get a list of missing doc list in one shot
    */

    private String showMissDocsList;

    private static final Logger Log = LogManager.getLogger(NortServiceImpl.class);

    /**
     * handles the doc retrieval exception. Performs checks to see if the
     * exception is a novus one and check the retry count.
     *
     * @param exception
     *            the exception that could be a novus one
     * @param novusRetryCounter
     *            the counter of occurred exceptions
     * @param retryCount
     *            the max retry count
     * @return the novusRetryCounter that is incremented
     * @throws Exception
     */
    public Integer handleException(final Exception exception, Integer novusRetryCounter, final Integer retryCount)
        throws Exception {
        if (isNovusException(exception)) {
            novusRetryCounter++;
            Log.error("Novus Exception has happened. Retry Count # is " + novusRetryCounter);
            if (novusRetryCounter == (retryCount - 1)) {
                throw exception;
            }
        } else {
            throw exception;
        }
        return novusRetryCounter;
    }

    /**
     * check if the exception is a novus exception.
     *
     * @param exception
     *            the exception that's thrown
     * @return Boolean if it is a novus Exception
     */
    public Boolean isNovusException(final Exception exception) {
        Boolean isNovusException = Boolean.FALSE;

        if (StringUtils.containsIgnoreCase(exception.getMessage(), "NOVUS")
            || (StringUtils.containsIgnoreCase(exception.getMessage(), "Cannot find the collection"))) {
            isNovusException = Boolean.TRUE;
        }
        return isNovusException;
    }

    @Required
    public void setDocRetryCount(final String docRetryCount) {
        this.docRetryCount = docRetryCount;
    }

    @Required
    public void setTocRetryCount(final String tocRetryCount) {
        this.tocRetryCount = tocRetryCount;
    }

    @Required
    public void setNortRetryCount(final String nortRetryCount) {
        this.nortRetryCount = nortRetryCount;
    }

    @Required
    public void setShowMissDocsList(final String showMissDocsList) {
        this.showMissDocsList = showMissDocsList;
    }

    public String getDocRetryCount() {
        return docRetryCount;
    }

    public String getTocRetryCount() {
        return tocRetryCount;
    }

    public String getNortRetryCount() {
        return nortRetryCount;
    }

    public String getShowMissDocsList() {
        return showMissDocsList;
    }

    public String getImgRetryCount() {
        return imgRetryCount;
    }

    @Required
    public void setImgRetryCount(final String imgRetryCount) {
        this.imgRetryCount = imgRetryCount;
    }
}
