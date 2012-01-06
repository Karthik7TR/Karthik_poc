package com.thomsonreuters.uscl.ereader.gather.image.domain;

/**
 * Wrapper for image download or metatdata fetch problems.
 */
public class ImageException extends Exception {

	private static final long serialVersionUID = 2856995610793806521L;
	
	public ImageException(Throwable cause) {
		super(cause);
	}
	public ImageException(String message) {
		super(message);
	}
	public ImageException(String message, Throwable cause) {
		super(message, cause);
	}
}
