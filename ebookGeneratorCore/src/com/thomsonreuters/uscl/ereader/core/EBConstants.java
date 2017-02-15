package com.thomsonreuters.uscl.ereader.core;

public class EBConstants
{
    /*** Environment, configuration   ***/
    public static final String COLLECTION_TYPE = "Collection";
    public static final String COLLECTION_SET_TYPE = "CollectionSet";
    public static final String XML_FILE_EXTENSION = ".xml";

    /** general***/

    public static final String GATHER_RESPONSE_OBJECT = "gatherResponse";

    public static final String GATHER_TOC_REQUEST_OBJECT = "gatherTocRequest";
    public static final String GATHER_NORT_REQUEST_OBJECT = "gatherNortRequest";
    public static final String GATHER_DOC_REQUEST_OBJECT = "gatherDocRequest";

    public static final String VIEW_RESPONSE = "responseView";

    public static final String URI_HOME = "/home.mvc";
    public static final String VIEW_HOME = "home";

    /*** Xml elements for toc.xml ***/
    public static final String TOC_XML_ELEMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
    public static final String TOC_START_EBOOK_ELEMENT = "<EBook>\r\n";
    public static final String TOC_END_EBOOK_ELEMENT = "</EBook>\r\n";
    public static final String TOC_START_EBOOKTOC_ELEMENT = "<EBookToc>";
    public static final String TOC_END_EBOOKTOC_ELEMENT = "</EBookToc>";
    public static final String TOC_START_NAME_ELEMENT = "<Name>";
    public static final String TOC_END_NAME_ELEMENT = "</Name>";
    public static final String TOC_START_DOCUMENT_GUID_ELEMENT = "<DocumentGuid>";
    public static final String TOC_END_DOCUMENT_GUID_ELEMENT = "</DocumentGuid>";
    public static final String TOC_START_GUID_ELEMENT = "<Guid>";
    public static final String TOC_END_GUID_ELEMENT = "</Guid>";
}
