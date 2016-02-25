/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Instances of this class represent the collection of document metadata for a given publishing run.
 * 
 * <p>Once created, DocumentMetadataAuthority instances are immutable.</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class DocumentMetadataAuthority {
	//this represents the document metadata record for a run of an ebook.
	private Set<DocMetadata> docMetadataSet = new HashSet<DocMetadata>();

	//these are keyed maps used to search for the corresponding metadata without hitting the database.
	private Map<String, DocMetadata> docMetadataKeyedByCite = new HashMap<String, DocMetadata>();
	private Map<Long, DocMetadata> docMetadataKeyedBySerialNumber = new HashMap<Long, DocMetadata>();
	private Map<String, DocMetadata> docMetadataKeyedByDocumentUuid = new HashMap<String, DocMetadata>();
	private Map<String, DocMetadata> docMetadataKeyedByPubIdAndPubPage = new HashMap<String, DocMetadata>();
	private Map<String, DocMetadata> docMetadataKeyedByProViewId = new HashMap<String, DocMetadata>();
	
	public DocumentMetadataAuthority (Set<DocMetadata> docMetadataSet){
		if (docMetadataSet == null) {
			throw new IllegalArgumentException("Cannot instantiate DocumentMetadataAuthority without a set of document metadata");
		}
		this.docMetadataSet = docMetadataSet;
		for (DocMetadata docMetadata : docMetadataSet) {

			if (docMetadata.getNormalizedFirstlineCite() == null && docMetadata.getFindOrig() != null)
			{
				// Prevent overwriting of value with duplicate find orig
				if(!docMetadataKeyedByCite.containsKey(docMetadata.getFindOrig())) 
				{
					//handle content that does not have normalized cite in metadata, for example: Sampson and Tindall
					docMetadataKeyedByCite.put(docMetadata.getFindOrig(), docMetadata);
				}
			}
			else
			{
				// Prevent overwriting of value with duplicate normalized firstline cite.
				if(!docMetadataKeyedByCite.containsKey(docMetadata.getNormalizedFirstlineCite())) 
				{
					docMetadataKeyedByCite.put(docMetadata.getNormalizedFirstlineCite(), docMetadata);
				}
			}
			
			// Prevent overwriting of value with duplicate serial number
			if(!docMetadataKeyedBySerialNumber.containsKey(docMetadata.getSerialNumber())) 
			{
				docMetadataKeyedBySerialNumber.put(docMetadata.getSerialNumber(), docMetadata);
			}
			
			docMetadataKeyedByDocumentUuid.put(docMetadata.getDocUuid(), docMetadata);
			docMetadataKeyedByProViewId.put(docMetadata.getProViewId(), docMetadata);
			
			if(StringUtils.isNotBlank(docMetadata.getFirstlineCitePubpage())) 
			{
				if (docMetadata.getFirstlineCitePubId() != null)
				{
					docMetadataKeyedByPubIdAndPubPage.put(docMetadata.getFirstlineCitePubId() + docMetadata.getFirstlineCitePubpage(), docMetadata);
				}
				if (docMetadata.getSecondlineCitePubId() != null)
				{
					docMetadataKeyedByPubIdAndPubPage.put(docMetadata.getSecondlineCitePubId() + docMetadata.getFirstlineCitePubpage(), docMetadata);
				}
				if (docMetadata.getThirdlineCitePubId() != null)
				{
					docMetadataKeyedByPubIdAndPubPage.put(docMetadata.getThirdlineCitePubId() + docMetadata.getFirstlineCitePubpage(), docMetadata);
				}
				
			}
		}
	}
	
	/**
	 * Retrieves a <em>read-only</em> copy of the document metadata for a given publishing run.
	 * 
	 * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
	 * @return the {@link DocMetadata} for all documents contained within the book.
	 */
	public Set<DocMetadata> getAllDocumentMetadata() {
		return Collections.unmodifiableSet(docMetadataSet);
	}

	/**
	 * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by normalized citation.
	 * 
	 * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
	 * @return the association between normalized citations and the corresponding {@link DocMetadata}
	 */
	public Map<String, DocMetadata> getDocMetadataKeyedByCite() {
		return Collections.unmodifiableMap(docMetadataKeyedByCite);
	}

	/**
	 * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by serial number.
	 * 
	 * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
	 * @return the association between serial numbers and the corresponding {@link DocMetadata}
	 */
	public Map<Long, DocMetadata> getDocMetadataKeyedBySerialNumber() {
		return Collections.unmodifiableMap(docMetadataKeyedBySerialNumber);
	}
	
	/**
	 * Returns a <em>read-only</em> {@link Map} of the {@link DocMetadata} keyed by document uuid.
	 * 
	 * <p>Note: the underlying {@link DocMetadata} instances are mutable, so use caution if they need to be modified.</p>
	 * @return the association between document uuids and the corresponding {@link DocMetadata}
	 */
	public Map<String, DocMetadata> getDocMetadataKeyedByDocumentUuid() {
		return Collections.unmodifiableMap(docMetadataKeyedByDocumentUuid);
	}
	
	public Map<String, DocMetadata> getDocMetadataKeyedByPubIdAndPubPage() {
		return Collections.unmodifiableMap(docMetadataKeyedByPubIdAndPubPage);
	}
	
	public Map<String, DocMetadata> getDocMetadataKeyedByProViewId() {
		return Collections.unmodifiableMap(docMetadataKeyedByProViewId);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
		
}
