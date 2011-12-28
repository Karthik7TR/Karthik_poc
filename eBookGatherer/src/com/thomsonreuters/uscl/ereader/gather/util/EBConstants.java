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
	public static final String NOVUSE_NVIRONMENT = "Client"; //"Prod";
	public static final String OUTPUT_TOC_FILE = "c:\\temp\\tocFile.txt";
	
	/*** String parsing operation ***/
	public static final String HEADING_START_TAG = "<heading>";
	public static final String HEADING_END_TAG =  "</heading>";
	
	/*** Xml elements ***/
	public static final String TOC_ROOT_ELEMENT = "EBook";
	public static final String TOC_ELEMENT = "EBookToc";
	public static final String NAME_ELEMENT = "Name";
	public static final String GUID_ELEMENT = "Guid";
	public static final String PARENT_GUID_ELEMENT ="ParentGuid";
	public static final String METADATA_ELEMENT = "Metadata";
	
}
