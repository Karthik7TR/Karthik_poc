package com.thomsonreuters.uscl.ereader.gather.exception;

public class GatherException extends Exception{
	
	private static final long serialVersionUID = -4259100376867069612L;
	
	private int errorCode = 0;

	public GatherException(String message, Throwable cause) {
        super(message, cause);
    }
	public GatherException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        setErrorCode(errorCode);
    }
	public GatherException(String message) {
		super(message);
	}
	public GatherException(String message, int errorCode) {
		super(message);
		setErrorCode(errorCode);
	}
	public int getErrorCode() {
		return errorCode;
	}
	private void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}

