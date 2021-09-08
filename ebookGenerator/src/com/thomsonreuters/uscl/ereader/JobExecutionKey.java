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
     * The path to the assembled eBook on NAS.
     */
    public static final String EBOOK_FILE = "eBookFile";

    /**
     * The path to the assembled eBook on NAS.
     */
    public static final String WORK_DIRECTORY = "workDirectory";

    public static final String BOOK_FILE_TYPE_SUFFIX = ".gz";

    public static final String GATHER_DIR = "gatherDir";

    /**
     * The path to the Novus gathered XML documents for this eBook on NAS.
     */
    public static final String GATHER_DOCS_DIR = "gatherDocsDir";
    public static final String GATHER_DOCS_METADATA_DIR = "gatherDocsMetadataDir";

    /** The TOC XML file */
    public static final String GATHER_TOC_DIR = "gatherTocDir";
    public static final String GATHER_TOC_FILE = "gatherTocFile";
    public static final String TRANSFORMED_TOC_DIR = "transformedTocDir";
    public static final String TRANSFORMED_TOC_FILE = "transformedTocFile";

    public static final String FORMAT_SPLITTOC_FILE = "formatSplitTocFile";

    public static final String CODES_WORKBENCH_ROOT_LANDING_STRIP_DIR = "rootCodesWorkbenchLandingStripDir";

    /**
     * Destination directory for images fetched from the Image Vertical REST service, these are considered dynamic images.
     */
    public static final String IMAGE_DYNAMIC_DEST_DIR = "imageDynamicDestDir";
    /**
     * Path to the flat text file that contains the list (one per line) of image GUID's that represent the physical
     * images that will be inserted into the book.  These GUID's serve as input to the Image Vertical REST service.
     */
    public static final String IMAGE_DYNAMIC_GUIDS_FILE = "imageDynamicGuidsFile";

    /**
     * Path to the flat text file that contains the list (one per line) of missing image GUID's.
     */
    public static final String IMAGE_MISSING_GUIDS_FILE = "imageMissingGuidsFile";

    /**
     * Path to the flat file that contains the mapping of all images in each document. This file is
     * used to append image metadata to the XML file before transformation.
     */
    public static final String IMAGE_TO_DOC_MANIFEST_FILE = "imageToDocManifestFile";
    /**
     * Path to the root destination directory for images, both static and dynamic
     */
    public static final String IMAGE_ROOT_DIR = "imageRootDir";
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
     * The path to the intermediate files generated by the Format Preprocess Service on the NAS.
     */
    public static final String FORMAT_PREPROCESS_DIR = "formatPreprocessedDir";

    /**
     * The path to the intermediate files generated by the Format Transformer Service on the NAS.
     */
    public static final String FORMAT_TRANSFORMED_DIR = "formatTransformedDir";

    /**
     * The path to the ImageMetadata generated files that are appended to the NOVUS xml to
     * be used during the image rendering.
     */
    public static final String FORMAT_IMAGE_METADATA_DIR = "formatImageMetadataDir";

    /**
     * The path to the splitEbook toc.xml files
     */
    public static final String SPLIT_EBOOK_TOC_DIR = "splitEbookTocDir";

    /**
     * The path to the splitEbook toc.xml files
     */
    public static final String SPLIT_EBOOK_DIR = "splitEbookDir";

    /**
     * The path to the post transformation files generated by the HTML Transformer Service on the NAS.
     */
    public static final String FORMAT_POST_TRANSFORM_DIR = "formatPostTransformDir";
    /**
     * The path to the post transformation files generated by the HTML Transformer Service on the NAS.
     */
    public static final String FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR = "formatCreatedLinksTransformDir";
    /**
     * The path to the final transformation files generated by the HTML Transformer Service on the NAS.
     */
    public static final String FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR = "formatFixedTransformDir";

    /**
     * The path to the intermediate files generated by the Format HTML Wrapper Service on the NAS.
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

    // Path to Format\splitEbook\splitNodeInfo.txt
    public static final String SPLIT_NODE_INFO_FILE = "splitNodeInfo";

    /**
     * Path to the flat text file that contains the list (one per line) of doc GUID's that represent the physical
     * docs that will be inserted into the book.  These GUID's serve as input to the Gather Vertical REST service.
     */
    public static final String DOCS_DYNAMIC_GUIDS_FILE = "docsDynamicGuidsFile";

    /**
     * Path to the flat text file that contains the list (one per line) of missing doc GUID's.
     */
    public static final String DOCS_MISSING_GUIDS_FILE = "docsMissingGuidsFile";

    /**
     * Path to the final destination for assets (images, stylesheets). Used by the assembly process.
     */
    public static final String ASSEMBLE_ASSETS_DIR = "assetsDir";

    /**
     * Book Definition Object. Used by different steps in the batch process
     */
    public static final String EBOOK_DEFINITION = "bookDefn";
    public static final String COMBINED_BOOK_DEFINITION = "combinedBookDefn";

    /**
     * Path to the directory with unpacked images.
     */
    public static final String XPP_IMAGES_UNPACK_DIR = "xppImagesUnpackDir";

    public static final String WITH_PAGE_NUMBERS = "withPageNumbers";
    public static final String WITH_INLINE_TOC = "withInlineToc";
    public static final String WITH_INLINE_INDEX = "withInlineIndex";
    public static final String WITH_THESAURUS = "withThesaurus";

    public static final String EXCEPTION_ON_GROUP_STEP_OCCURRED = "exceptionOnGroupStepOccurred";

    public static final String PAGE_VOLUMES_SET = "pageVolumesSet";

    public static final String HAS_FILE_SOURCE_TYPE = "hasFileSourceType";

}
