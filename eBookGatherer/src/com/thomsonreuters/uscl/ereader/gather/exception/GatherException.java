package com.thomsonreuters.uscl.ereader.gather.exception;

public class GatherException extends Exception{
	
	private static final long serialVersionUID = -4259100376867069612L;
	
	private int errorCode = 0;

	public GatherException(String message, Throwable cause) {
        super(message, cause);
    }
	public GatherException(String message) {
		super(message);
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}

