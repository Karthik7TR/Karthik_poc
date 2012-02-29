package com.thomsonreuters.uscl.ereader.gather.services;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class NovusUtility {
	
	
    /**
     * Retry count. default value 3
     */
   private String docRetryCount;
   private String tocRetryCount;
   private String nortRetryCount;	
	
	
	private static final Logger Log = Logger.getLogger(NortServiceImpl.class);	

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
	public Integer handleException(final Exception exception,
			Integer novusRetryCounter, final Integer retryCount)
			throws Exception {
		if (isNovusException(exception)) {
			novusRetryCounter++;
			Log.error("Novus Exception has happened. Retry Count # is "
					+ novusRetryCounter);
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
	
		if (StringUtils.containsIgnoreCase(exception.getMessage(), "NOVUS")) {
			isNovusException = Boolean.TRUE;
		}
		return isNovusException;
	}

	@Required
	public void setDocRetryCount(String docRetryCount) {
		this.docRetryCount = docRetryCount;
	}

	@Required
	public void setTocRetryCount(String tocRetryCount) {
		this.tocRetryCount = tocRetryCount;
	}
	
	@Required
	public void setNortRetryCount(String nortRetryCount) {
		this.nortRetryCount = nortRetryCount;
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

}
