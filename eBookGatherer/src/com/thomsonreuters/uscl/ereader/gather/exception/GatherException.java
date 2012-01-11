package com.thomsonreuters.uscl.ereader.gather.exception;

public class GatherException extends Exception{
	
private static final long serialVersionUID = 1L;
    
	public GatherException(String message, Throwable cause) {
        super(message, cause);
    }

	public GatherException(String message) {
		super(message);
	}
}

