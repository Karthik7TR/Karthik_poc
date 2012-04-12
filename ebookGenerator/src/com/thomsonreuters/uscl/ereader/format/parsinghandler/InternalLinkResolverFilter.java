/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;

/**
 * A SAX Filter that detects whether or not an anchor points to a resource included within an eBook.
 * 
 * <p>If there is a match, the URL is converted to ProView format &lt;a href="er:#id"&gt;anchor text&lt;/a&gt;.</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class InternalLinkResolverFilter extends XMLFilterImpl {
	private static final String ANCHOR_ELEMENT = "a";
	private static final String URI = "";
	private static final String HREF = "href";
	
	private DocumentMetadataAuthority documentMetadataAuthority;
	
	private static final Pattern NORMALIZED_CITE_URL_PATTERN = Pattern.compile(".*FullText.*cite=([^&]+)");
	
	private static final Pattern NORMALIZED_CITE_UUID_URL_PATTERN = Pattern.compile(".*FullText.*cite=(UUID[^&]+)");
	
	private static final int RESULT_GROUP = 1;
	
	private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile(".*FullText.*serNum=([^&]+)");
	private static final Pattern DOCUMENT_UUID_PATTERN = Pattern.compile(".*FullText.*([a-zA-Z]{1}[a-fA-F0-9]{10}[-]?[a-fA-F0-9]{11}[-]?[a-fA-F0-9]{11}).*");
	private static final Pattern NEXT_VIEW_DOCUMENT_URL_PATTERN = Pattern.compile(".*/document/([^/]+)/view.*");
	private static final String UTF8_ENCODING = "utf-8";
	private static final String PROVIEW_ASSERT_REFERENCE_PREFIX = "er:#";
	
	public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority) {
		if (documentMetadataAuthority == null) {
			throw new IllegalArgumentException ("Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority");
		}
		this.documentMetadataAuthority = documentMetadataAuthority;
	}


	@Override
	public void startElement(String uri, String localname, String qName, Attributes attributes) throws SAXException {
		String resourceUrl = attributes.getValue(HREF);
		if (ANCHOR_ELEMENT.equals(qName) && StringUtils.isNotBlank(resourceUrl)) {
			//we're in an anchor that has a resouce reference (href). 
			//Determine if the anchor is internal or not and, if it is, resolve the URL.
			if (isNormalizedCiteUuidUrl(resourceUrl)) {
				
				String normalizedCite = getNormalizedCiteUuid(resourceUrl);
				Attributes resolvedAttributes = resolveDocumentUuidReference(normalizedCite, attributes);
				super.startElement(uri, localname, qName, resolvedAttributes);
			}
			else if (isNormalizedCiteUrl(resourceUrl)) {
				String normalizedCite = getNormalizedCite(resourceUrl);
				Attributes resolvedAttributes = resolveNormalizedCiteReference(normalizedCite, attributes);
				super.startElement(uri, localname, qName, resolvedAttributes);
			}
			else if (isSerialNumberUrl(resourceUrl)) {
				String serialNumber = getSerialNumber(resourceUrl);
				Attributes resolvedAttributes = resolveSerialNumberReference(serialNumber, attributes);
				super.startElement(uri, localname, qName, resolvedAttributes);
			}
			else if (isDocumentUrl(resourceUrl)) {
				String documentUuid = getDocumentUuid(resourceUrl);
				Attributes resolvedAttributes = resolveDocumentUuidReference(documentUuid, attributes);
				super.startElement(uri, localname, qName, resolvedAttributes);
			}
			else { //we don't know how to convert the href, so leave it untouched.
				super.startElement(uri, localname, qName, attributes);
			}
			
		}
		else { //this tag is not an anchor, so leave it untouched.
			super.startElement(uri, localname, qName, attributes);			
		}
	}

	protected Attributes resolveDocumentUuidReference(String documentUuid, Attributes attributes) {
		DocMetadata docMetadata = documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(documentUuid);
		AttributesImpl resolvedAttributes = new AttributesImpl(attributes);
		if (docMetadata == null){
		    return resolvedAttributes;
		}
				 
		String ebookResourceIdentifier = docMetadata.getDocFamilyUuid();
		
		int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
		resolvedAttributes.setValue(anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value
		return resolvedAttributes;
	}

	protected String getDocumentUuid(String resourceUrl) {
		Matcher matcher = DOCUMENT_UUID_PATTERN.matcher(resourceUrl);
		matcher.find();
		return matcher.group(RESULT_GROUP);
	}

	protected boolean isDocumentUrl(String resourceUrl) {
		return DOCUMENT_UUID_PATTERN.matcher(resourceUrl).find();
	}

	protected Attributes resolveSerialNumberReference(String serialNumber, Attributes attributes) {
		Integer serNum = Integer.valueOf(serialNumber);
		DocMetadata docMetadata = documentMetadataAuthority.getDocMetadataKeyedBySerialNumber().get(serNum);
		
		AttributesImpl resolvedAttributes = new AttributesImpl(attributes);
		if (docMetadata == null){
		    return resolvedAttributes;
		}
		
		String ebookResourceIdentifier = docMetadata.getDocFamilyUuid();
				
		int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
		
		resolvedAttributes.setValue(anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value
		
		return resolvedAttributes;
	}

	protected String getSerialNumber(String resourceUrl) {
		Matcher matcher = SERIAL_NUMBER_PATTERN.matcher(resourceUrl);
		matcher.find();
		return matcher.group(RESULT_GROUP);
	}

	protected boolean isSerialNumberUrl(String resourceUrl) {
		return SERIAL_NUMBER_PATTERN.matcher(resourceUrl).find();
	}

	/**
	 * Parses and attempts to identify a normalized cite given a URL.
	 *  
	 * @param resourceUrl
	 * @return
	 * @throws SAXException if a data error o
	 */
	protected String getNormalizedCite(String resourceUrl) throws SAXException {
		Matcher matcher = NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl);
		matcher.find();
		String normalizedCite = "";
		
		try {
			normalizedCite = URLDecoder.decode(matcher.group(RESULT_GROUP), UTF8_ENCODING);
		}
		catch (UnsupportedEncodingException e) {
			throw new SAXException(UTF8_ENCODING + " encoding not supported when attempting to parse normalized cite from URL: " + resourceUrl, e);
		}
		normalizedCite = normalizedCite.replace(String.valueOf((char) 0xA7),"s"); // need to check other special characters
		return normalizedCite;
	}

	/**
	 * Parses and attempts to identify a normalized cite uuid given a URL.
	 *  
	 * @param resourceUrl
	 * @return
	 * @throws SAXException if a data error o
	 */
	protected String getNormalizedCiteUuid(String resourceUrl) throws SAXException {
		Matcher matcher = NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl);
		matcher.find();
		String normalizedCite = "";
		
		try {
			String citeUuid = URLDecoder.decode(matcher.group(RESULT_GROUP), UTF8_ENCODING);
			normalizedCite = citeUuid.split("\\(")[1].split("\\)")[0].trim();			
		}
		catch (UnsupportedEncodingException e) {
			throw new SAXException(UTF8_ENCODING + " encoding not supported when attempting to parse normalized cite from URL: " + resourceUrl, e);
		}
		return normalizedCite;
	}
	
	/**
	 * Determines if the url matches a known normalized cite pattern.
	 * 
	 * @param resourceUrl the URL to compare to the pattern.
	 * @return true if the url is a normalized cite URL, false otherwise.
	 */
	protected boolean isNormalizedCiteUrl(String resourceUrl) {
		return NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl).find();
	}
	
	/**
	 * Determines if the url matches a known normalized cite uuid pattern.
	 * 
	 * @param resourceUrl the URL to compare to the pattern.
	 * @return true if the url is a normalized cite Uuid URL, false otherwise.
	 */
	protected boolean isNormalizedCiteUuidUrl(String resourceUrl) {
		return NORMALIZED_CITE_UUID_URL_PATTERN.matcher(resourceUrl).find();
		
	}

	/**
	 * Resolves a normalized cite reference to a WLN resource to an internal book reference, provided it exists in the normalizedCiteAuthority.
	 * 
	 * @param normalizedCite the normalizedCite to match against the keys in the normalizedCiteAuthority.
	 * @param attributes 
	 * @return the updated URL in ProView format.
	 */
	protected Attributes resolveNormalizedCiteReference(final String normalizedCite, Attributes attributes) {
        		 
		DocMetadata docMetadata = documentMetadataAuthority.getDocMetadataKeyedByCite().get(normalizedCite);
		
		AttributesImpl resolvedAttributes = new AttributesImpl(attributes);
		if (docMetadata == null){
		    return resolvedAttributes;
		}				
				
		String ebookResourceIdentifier = docMetadata.getDocFamilyUuid();
						
		int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
		resolvedAttributes.setValue(anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value
		
		return resolvedAttributes;
	}
}
