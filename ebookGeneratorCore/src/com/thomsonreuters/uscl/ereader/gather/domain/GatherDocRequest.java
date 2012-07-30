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
	private boolean isFinalStage;
	
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
							File metadataDestinationDirectory,
							boolean isFinalStage) {
		setGuids(guids);
		setCollectionName(collectionName);
		setContentDestinationDirectory(contentDestinationDirectory);
		setMetadataDestinationDirectory(metadataDestinationDirectory);
		setFinalStage(isFinalStage);
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
	public boolean isFinalStage() {
		return isFinalStage;
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
	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collectionName == null) ? 0 : collectionName.hashCode());
		result = prime
				* result
				+ ((contentDestinationDirectory == null) ? 0
						: contentDestinationDirectory.hashCode());
		result = prime * result + ((guids == null) ? 0 : guids.hashCode());
		result = prime * result + (isFinalStage ? 1231 : 1237);
		result = prime
				* result
				+ ((metadataDestinationDirectory == null) ? 0
						: metadataDestinationDirectory.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GatherDocRequest other = (GatherDocRequest) obj;
		if (collectionName == null) {
			if (other.collectionName != null)
				return false;
		} else if (!collectionName.equals(other.collectionName))
			return false;
		if (contentDestinationDirectory == null) {
			if (other.contentDestinationDirectory != null)
				return false;
		} else if (!contentDestinationDirectory
				.equals(other.contentDestinationDirectory))
			return false;
		if (guids == null) {
			if (other.guids != null)
				return false;
		} else if (!guids.equals(other.guids))
			return false;
		if (isFinalStage != other.isFinalStage)
			return false;
		if (metadataDestinationDirectory == null) {
			if (other.metadataDestinationDirectory != null)
				return false;
		} else if (!metadataDestinationDirectory
				.equals(other.metadataDestinationDirectory))
			return false;
		return true;
	}
}
