/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader;

/**
 * This class represents the names of well-known business keys used within the Spring Batch JobExecutionContext.
 * 
 * <p>The intent is that developers of steps specify the properties that they depend upon in this class, and 
 * use these names to retrieve those property values from the Spring Batch Job Execution Context.</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class JobExecutionKey {
	
	/**
	 * The path from which to assemble the eBook from.
	 */
	public static final String EBOOK_DIRECTORY = "eBookDirectory";
	
	/**
	 * The path to the assembled eBook on NAS.
	 */
	public static final String EBOOK_FILE = "eBookFile";
	
	/**
	 * The path to the Novus gathered XML documents for this eBook on NAS.
	 */
	public static final String GATHER_DOCS_DIR = "gatherDocsDir";
	
	/** The TOC XML file */
	public static final String GATHER_TOC_FILE = "gatherTocFile";
	
	/**
	 * Destination directory for images fetched from the Image Vertical REST service, these are considered dynamic images.
	 */
	public static final String IMAGE_DYNAMIC_DEST_DIR = "imageDynamicDestDir";
	/**
	 * Path to the flat text file that contatins the list (one per line) of image GUID's that represent the physical
	 * images that will be inserted into the book.  These GUID's serve as input to the Image Vertical REST service.
	 */
	public static final String IMAGE_DYNAMIC_GUIDS_FILE = "imageDynamicGuidsFile";
	/**
	 * Path to the root destination directory for images, both static and dynamic
	 */
	public static final String IMAGE_ROOT_DIR = "imageRootDir";
	/**
	 * Destination directory for images fetched from the static image filesystem tree, these are considered static images.
	 */
	public static final String IMAGE_STATIC_DEST_DIR = "imageStaticDestDir";
	/**
	 * The manifest file that contains image basenames, one per line, that describe which
	 * static images are to be copied to the static image destination directory. 
	 */
	public static final String IMAGE_STATIC_MANIFEST_FILE = "imageStaticManifestFile";

	/**
	 * Path to the flat text file that contatins the list (one per line) of image GUID's that represent the physical
	 * images that will be inserted into the book. 
	 */
	
	/**
	 * The path to the intermediate files generated by the Format Transformer Service for this eBook on NAS.
	 */
	public static final String FORMAT_TRANSFORMED_DIR = "formatTransformedDir";
	
	/**
	 * The path to the intermediate files generated by the Format HTML Wrapper Service for this eBook on NAS.
	 */
	public static final String FORMAT_HTML_WRAPPER_DIR = "formatHtmlWrapperDir";
	
	/**
	 * The path to the documents that are to be included in the published book. 
	 * 
	 * <em>The last step of format is responsible for setting this property.</em>
	 */
	public static final String FORMAT_DOCUMENTS_READY_DIRECTORY_PATH = "formatDocumentsReadyDirectoryPath";
	
	/**
	 * The number of documents that were initially retrieved from Novus, used as a sanity check after many SpringBatch steps.
	 */
	public static final String EBOOK_STATS_DOC_COUNT = "eBookStatsDocCount";
	
	/**
	 * The file path to the cover art on NAS.
	 */
	public static final String COVER_ART_PATH = "coverArtPath";
	

	
	/**
	 * The path to title.xml within the assemble directory.  The GenerateTitleMetadata step writes the title metadata to this file.
	 */
	public static final String TITLE_XML_FILE = "titleXmlFile";
}
