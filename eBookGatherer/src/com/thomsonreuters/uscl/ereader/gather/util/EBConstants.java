/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.util;

public class EBConstants 
{

	/*** Environment, configuration   ***/
	public static final String COLLECTION_TYPE = "Collection";
	public static final String COLLECTION_SET_TYPE = "CollectionSet";
	public static final String XML_FILE_EXTENSION = ".xml";
	public static final String TOC_XML_BASE_NAME = "toc" + XML_FILE_EXTENSION;
	
	
	/*** String parsing operation ***/
	public static final String HEADING_START_TAG = "<heading>";
	public static final String HEADING_END_TAG =  "</heading>";
	
	/*** Xml elements ***/
	public static final String TOC_ROOT_ELEMENT = "EBook";
	public static final String TOC_ELEMENT = "EBookToc";
	public static final String NAME_ELEMENT = "Name";
	public static final String GUID_ELEMENT = "Guid";
	public static final String PARENT_GUID_ELEMENT ="ParentGuid";
	public static final String DOCUMENT_GUID_ELEMENT ="DocumentGuid";
	public static final String METADATA_ELEMENT = "Metadata";
	
	/** general***/
	
	public static final String GATHER_RESPONSE_OBJECT = "gatherResponse";
	
	public static final String GATHER_TOC_REQUEST_OBJECT = "gatherTocRequest";
	public static final String GATHER_DOC_REQUEST_OBJECT = "gatherDocRequest";
	
	public static final String VIEW_RESPONSE = "responseView";
	
	
	
	
}

