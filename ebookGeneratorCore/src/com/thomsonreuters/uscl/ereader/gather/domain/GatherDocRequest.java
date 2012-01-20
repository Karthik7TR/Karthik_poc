/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A HTTP request body to the Gather document REST service.
 * Eventually serialized into XML for transmission over the wire.
 */
public class GatherDocRequest implements Serializable {
	private static final long serialVersionUID = 3753175682349575546L;
	
	/**
	 * Document GUID's, the document key.
	 * This is an ArrayList (as opposed to a Collection)because JiBX required a concrete class.
	 */
	private ArrayList<String> guids;
	/** Document collection name */
	private String collectionName;
	/** Filesystem directory where document content will be placed as guid.xml */
	private File contentDestinationDirectory;
	/** Filesystem directory where document metadata will be placed as guid.xml */
	private File metadataDestinationDirectory;
	
	public GatherDocRequest() {
		super();
	}

	/**
	 * Full constructor for document requests to Gather REST service.
	 * @param guid the document key
	 * @param collectionName
	 * @param destinationDirectory filesystem directory where created the XML document files are to be placed
	 */
	public GatherDocRequest(Collection<String> guids, String collectionName,
							File contentDestinationDirectory,
							File metadataDestinationDirectory) {
		setGuids(guids);
		setCollectionName(collectionName);
		setContentDestinationDirectory(contentDestinationDirectory);
		setMetadataDestinationDirectory(metadataDestinationDirectory);
	}
	public Collection<String> getGuids() {
		return guids;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public File getContentDestinationDirectory() {
		return contentDestinationDirectory;
	}
	public File getMetadataDestinationDirectory() {
		return metadataDestinationDirectory;
	}
	public void setGuids(Collection<String> guidCollection) {
		this.guids = new ArrayList<String>(guidCollection);
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public void setContentDestinationDirectory(File destinationDirectory) {
		this.contentDestinationDirectory = destinationDirectory;
	}
	public void setMetadataDestinationDirectory(File destinationDirectory) {
		this.metadataDestinationDirectory = destinationDirectory;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
