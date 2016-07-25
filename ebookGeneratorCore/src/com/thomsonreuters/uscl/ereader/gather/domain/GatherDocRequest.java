/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A HTTP request body to the Gather document REST service. Eventually
 * serialized into XML for transmission over the wire.
 */
@XmlRootElement(name = "gatherDocRequest", namespace="com.thomsonreuters.uscl.ereader.gather.domain")
public class GatherDocRequest implements Serializable {
	private static final long serialVersionUID = -3445724162855653304L;

	/**
	 * Document GUID's, the document key. This is an ArrayList (as opposed to a
	 * Collection)because JiBX required a concrete class.
	 */
	private ArrayList<String> guids;
	/** Document collection name */
	private String collectionName;
	/**
	 * Filesystem directory where document content will be placed as guid.xml
	 */
	private File contentDestinationDirectory;
	/**
	 * Filesystem directory where document metadata will be placed as guid.xml
	 */
	private File metadataDestinationDirectory;
	private boolean isFinalStage;
	private boolean useReloadContent;

	public GatherDocRequest() {
		super();
	}

	/**
	 * Full constructor for document requests to Gather REST service.
	 * 
	 * @param guid
	 *            the document key
	 * @param collectionName
	 * @param destinationDirectory
	 *            filesystem directory where created the XML document files are
	 *            to be placed
	 */
	public GatherDocRequest(Collection<String> guids, String collectionName, File contentDestinationDirectory,
			File metadataDestinationDirectory, boolean isFinalStage, boolean useReloadContent) {
		setGuids(guids);
		setCollectionName(collectionName);
		setContentDestinationDirectory(contentDestinationDirectory);
		setMetadataDestinationDirectory(metadataDestinationDirectory);
		setIsFinalStage(isFinalStage);
		setUseReloadContent(useReloadContent);
	}

	public boolean getUseReloadContent() {
		return useReloadContent;
	}

	@XmlElement(name = "useReloadContent", required = true)
	public void setUseReloadContent(boolean useReloadContent) {
		this.useReloadContent = useReloadContent;
	}

	public Collection<String> getGuids() {
		return guids;
	}

	@XmlElementWrapper(name = "docGuids", required = true)
	public void setGuids(Collection<String> guidCollection) {
		this.guids = new ArrayList<String>(guidCollection);
	}

	public String getCollectionName() {
		return collectionName;
	}

	@XmlElement(name = "collectionName", required = false)
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public File getContentDestinationDirectory() {
		return contentDestinationDirectory;
	}

	@XmlElement(name = "contentDestinationDirectory", required = true)
	public void setContentDestinationDirectory(File destinationDirectory) {
		this.contentDestinationDirectory = destinationDirectory;
	}

	public File getMetadataDestinationDirectory() {
		return metadataDestinationDirectory;
	}

	@XmlElement(name = "metadataDestinationDirectory", required = true)
	public void setMetadataDestinationDirectory(File destinationDirectory) {
		this.metadataDestinationDirectory = destinationDirectory;
	}

	public boolean getIsFinalStage() {
		return isFinalStage;
	}

	@XmlElement(name = "isFinalStage", required = true)
	public void setIsFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectionName == null) ? 0 : collectionName.hashCode());
		result = prime * result + ((contentDestinationDirectory == null) ? 0 : contentDestinationDirectory.hashCode());
		result = prime * result + ((guids == null) ? 0 : guids.hashCode());
		result = prime * result + (isFinalStage ? 1231 : 1237);
		result = prime * result
				+ ((metadataDestinationDirectory == null) ? 0 : metadataDestinationDirectory.hashCode());
		result = prime * result + (useReloadContent ? 1231 : 1237);
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
		} else if (!contentDestinationDirectory.equals(other.contentDestinationDirectory))
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
		} else if (!metadataDestinationDirectory.equals(other.metadataDestinationDirectory))
			return false;
		if (useReloadContent != other.useReloadContent)
			return false;
		return true;
	}
}
