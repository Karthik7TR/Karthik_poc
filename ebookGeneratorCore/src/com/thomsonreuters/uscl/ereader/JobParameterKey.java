package com.thomsonreuters.uscl.ereader;


/**
 * These are the keys used to access job launch parameters from the JobParameters object.
 * These key/value pairs have been loaded prior to the launch of the job
 *
 */
public class JobParameterKey {
	
	// ===== Mapped from BookDefinition ===============
	public static final String AUTHORS = "authors";
	public static final String BOOK_NAME = "bookName";
	public static final String CONTENT_SUBTYPE = "contentSubtype";
	public static final String CONTENT_TYPE = "contentType";
	public static final String COPYRIGHT = "copyright";
	public static final String COVER_IMAGE = "coverImage";
	public static final String DOC_COLLECTION_NAME = "docCollectionName";
	public static final String ISBN = "isbn";
	public static final String MAJOR_VERSION = "majorVersion";
	public static final String MATERIAL_ID = "materialId";
	public static final String MATERIAL_ID_EMBEDDED_IN_DOC_TEXT = "materialIdEmbeddedInDocText";
	public static final String MINOR_VERSION = "minorVersion";
	public static final String NORT_DOMAIN = "nortDomain";
	public static final String NORT_FILTER_VIEW = "nortFilterView";
	public static final String ROOT_TOC_GUID = "rootTocGuid";
	/** The rightmost name component of a slash separated title ID fully-qualified name, like "ak_2013_state". */
	public static final String TITLE_ID = "titleId";
	/** The complete title ID name, like "uscl/cr/ak_2013_state" */
	public static final String TITLE_ID_FULLY_QUALIFIED = "titleIdFullyQualified";
	public static final String TOC_COLLECTION_NAME = "tocCollectionName";
	
	// ===== Standard values from job request and misc =====
	/** Who started the job. */
	public static final String USER_NAME = "userName";
	/** E-mail address of the person who started the job. */
	public static final String USER_EMAIL = "userEmail";
	/** Host name on which Spring Batch is running the job. */
	public static final String HOST_NAME = "hostName";
	/**
	 * Serves as a unique serial number to differentiate the job instances.
	 * If you don't have a varying job parameter for the instance, you get an exception when you try an launch the job
	 * since it thinks that the e-book generating job has already run to completion.
	 */
	public static final String JOB_TIMESTAMP = "jobTimestamp";
}
