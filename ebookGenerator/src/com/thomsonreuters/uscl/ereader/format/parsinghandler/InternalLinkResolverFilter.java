/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.util.CitationNormalizationRulesUtil;


/**
 * A SAX Filter that detects whether or not an anchor points to a resource included within an
 * eBook.<p>If there is a match, the URL is converted to ProView format &lt;a
 * href="er:#id"&gt;anchor text&lt;/a&gt;.</p>
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *  
 */
public class InternalLinkResolverFilter extends XMLFilterImpl
{
	private static final Logger LOG = Logger.getLogger(InternalLinkResolverFilter.class);
	private static final String ANCHOR_ELEMENT = "a";
    private static final String HREF = "href";
    private static final Pattern NORMALIZED_CITE_URL_PATTERN =
        Pattern.compile(".*FullText.*cite=([^&]+)");
    private static final Pattern NORMALIZED_CITE_UUID_URL_PATTERN =
        Pattern.compile(".*FullText.*cite=(UUID[^&]+)");
    private static final int RESULT_GROUP = 1;
    private static final Pattern SERIAL_NUMBER_PATTERN =
        Pattern.compile(".*FullText.*serNum=([^&]+)");
    private static final Pattern DOCUMENT_UUID_PATTERN =
        Pattern.compile(
            ".*FullText.*([a-zA-Z]{1}[a-fA-F0-9]{10}[-]?[a-fA-F0-9]{11}[-]?[a-fA-F0-9]{11}).*");
    private static final String UTF8_ENCODING = "utf-8";
    private static final String PROVIEW_ASSERT_REFERENCE_PREFIX = "er:#";
    private DocumentMetadataAuthority documentMetadataAuthority;
    private File docsGuidFile ;

    public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority )
    {
        if (documentMetadataAuthority == null)
        {
            throw new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority");
        }

        this.documentMetadataAuthority = documentMetadataAuthority;       
    }
    
    public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority , final File docsGuidFile)
    {
        if (documentMetadataAuthority == null)
        {
            throw new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority");
        }

        this.documentMetadataAuthority = documentMetadataAuthority;
        this.docsGuidFile = docsGuidFile;
    }

    @Override
    public void startElement(String uri, String localname, String qName, Attributes attributes)
        throws SAXException
    {
        String resourceUrl = attributes.getValue(HREF);

        if (ANCHOR_ELEMENT.equals(qName) && StringUtils.isNotBlank(resourceUrl))
        {
            String linkParameter = getExtraParameter(resourceUrl);

            //we're in an anchor that has a resouce reference (href). 
            //Determine if the anchor is internal or not and, if it is, resolve the URL.
            if (isNormalizedCiteUuidUrl(resourceUrl))
            {
                String normalizedCiteUuid = getNormalizedCiteUuid(resourceUrl);
                Attributes resolvedAttributes =
                    resolveDocumentUuidReference(normalizedCiteUuid, attributes, linkParameter);
                super.startElement(uri, localname, qName, resolvedAttributes);
            }
            else if (isNormalizedCiteUrl(resourceUrl))
            {
                String normalizedCite = getNormalizedCite(resourceUrl);
                Attributes resolvedAttributes =
                    resolveNormalizedCiteReference(normalizedCite, attributes, linkParameter);
                super.startElement(uri, localname, qName, resolvedAttributes);
            }
            else if (isSerialNumberUrl(resourceUrl))
            {
                String serialNumber = getSerialNumber(resourceUrl);
                Attributes resolvedAttributes =
                    resolveSerialNumberReference(serialNumber, attributes, linkParameter);
                super.startElement(uri, localname, qName, resolvedAttributes);
            }
            else if (isDocumentUrl(resourceUrl))
            {
                String documentUuid = getDocumentUuid(resourceUrl);
                Attributes resolvedAttributes =
                    resolveDocumentUuidReference(documentUuid, attributes, linkParameter);
                super.startElement(uri, localname, qName, resolvedAttributes);
            }
            else
            { //we don't know how to convert the href, so leave it untouched.
                super.startElement(uri, localname, qName, attributes);
            }
        }
        else
        { //this tag is not an anchor, so leave it untouched.
            super.startElement(uri, localname, qName, attributes);
        }
    }

    protected String getDocumentUuid(String resourceUrl)
    {
        Matcher matcher = DOCUMENT_UUID_PATTERN.matcher(resourceUrl);
        matcher.find();

        return matcher.group(RESULT_GROUP);
    }

    /**
     * Parses and attempts to identify a normalized cite given a URL.
     *
     * @param resourceUrl
     *
     * @return
     *
     * @throws SAXException if a data error o
     */
    protected String getNormalizedCite(String resourceUrl)
        throws SAXException
    {
        Matcher matcher = NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl);
        matcher.find();

        String normalizedCite = "";

        try
        {
            normalizedCite = URLDecoder.decode(matcher.group(RESULT_GROUP), UTF8_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new SAXException(
                UTF8_ENCODING
                + " encoding not supported when attempting to parse normalized cite from URL: "
                + resourceUrl, e);
        }

        normalizedCite = CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);

        return normalizedCite;
    }

    /**
     * Parses and attempts to identify a normalized cite uuid given a URL.
     *
     * @param resourceUrl
     *
     * @return
     *
     * @throws SAXException if a data error o
     */
    protected String getNormalizedCiteUuid(String resourceUrl)
        throws SAXException
    {
        Matcher matcher = NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl);
        matcher.find();

        String normalizedCite = "";

        try
        {
            String citeUuid = URLDecoder.decode(matcher.group(RESULT_GROUP), UTF8_ENCODING);
            normalizedCite = citeUuid.split("\\(")[1].split("\\)")[0].trim();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new SAXException(
                UTF8_ENCODING
                + " encoding not supported when attempting to parse normalized cite from URL: "
                + resourceUrl, e);
        }

        return normalizedCite;
    }

    protected String getSerialNumber(String resourceUrl)
    {
        Matcher matcher = SERIAL_NUMBER_PATTERN.matcher(resourceUrl);
        matcher.find();

        return matcher.group(RESULT_GROUP);
    }

    protected boolean isDocumentUrl(String resourceUrl)
    {
        return DOCUMENT_UUID_PATTERN.matcher(resourceUrl).find();
    }

    /**
     * Determines if the url matches a known normalized cite pattern.
     *
     * @param resourceUrl the URL to compare to the pattern.
     *
     * @return true if the url is a normalized cite URL, false otherwise.
     */
    protected boolean isNormalizedCiteUrl(String resourceUrl)
    {
        return NORMALIZED_CITE_URL_PATTERN.matcher(resourceUrl).find();
    }

    /**
     * Determines if the url matches a known normalized cite uuid pattern.
     *
     * @param resourceUrl the URL to compare to the pattern.
     *
     * @return true if the url is a normalized cite Uuid URL, false otherwise.
     */
    protected boolean isNormalizedCiteUuidUrl(String resourceUrl)
    {
        return NORMALIZED_CITE_UUID_URL_PATTERN.matcher(resourceUrl).find();
    }

    protected boolean isSerialNumberUrl(String resourceUrl)
    {
        return SERIAL_NUMBER_PATTERN.matcher(resourceUrl).find();
    }

    protected Attributes resolveDocumentUuidReference (
        String documentUuid, Attributes attributes, String linkParameter)
    {
        DocMetadata docMetadata =
            documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid().get(documentUuid);
        AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (docMetadata == null)
        {
            return resolvedAttributes;
        }

        String ebookResourceIdentifier = docMetadata.getProViewId();

        if (!"".equals(linkParameter))
        {
            ebookResourceIdentifier = ebookResourceIdentifier + "/" + linkParameter;
        }
      
        String tocGuid = getTitleXMLTOCFilter(documentUuid);

        if (!"".equals(tocGuid) && "".equals(linkParameter))
        {
        	ebookResourceIdentifier = ebookResourceIdentifier + "/" + tocGuid;
        }
        int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
        resolvedAttributes.setValue(
            anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }

    /**
     * Resolves a normalized cite reference to a WLN resource to an internal book
     * reference, provided it exists in the normalizedCiteAuthority.
     *
     * @param normalizedCite the normalizedCite to match against the keys in the
     *        normalizedCiteAuthority.
     * @param attributes
     *
     * @return the updated URL in ProView format.
     */
    protected Attributes resolveNormalizedCiteReference(
        final String normalizedCite, Attributes attributes, String linkParameter)
    {
        DocMetadata docMetadata =
            documentMetadataAuthority.getDocMetadataKeyedByCite().get(normalizedCite);

        AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (docMetadata == null)
        {
            return resolvedAttributes;
        }

        String ebookResourceIdentifier = docMetadata.getProViewId();

        if (!"".equals(linkParameter))
        {
            ebookResourceIdentifier = ebookResourceIdentifier + "/" + linkParameter;
        }
        

        String tocGuid = getTitleXMLTOCFilter(docMetadata.getDocUuid());

        if (!"".equals(tocGuid) && "".equals(linkParameter))
        {
        	ebookResourceIdentifier = ebookResourceIdentifier + "/" + tocGuid;
        }

        int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
        resolvedAttributes.setValue(
            anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }

    protected Attributes resolveSerialNumberReference(
        String serialNumber, Attributes attributes, String linkParameter)
    {
        Integer serNum = Integer.valueOf(serialNumber);
        DocMetadata docMetadata =
            documentMetadataAuthority.getDocMetadataKeyedBySerialNumber().get(serNum);

        AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (docMetadata == null)
        {
            return resolvedAttributes;
        }

        String ebookResourceIdentifier = docMetadata.getProViewId();

        if (!"".equals(linkParameter))
        {
            ebookResourceIdentifier = ebookResourceIdentifier + "/" + linkParameter;
        }
        
        String tocGuid = getTitleXMLTOCFilter(docMetadata.getDocUuid());

        if (!"".equals(tocGuid) && "".equals(linkParameter))
        {
        	ebookResourceIdentifier = ebookResourceIdentifier + "/" + tocGuid;
        }

        int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);

        resolvedAttributes.setValue(
            anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }

    /**
     * 
     * @param url
     *
     * @return Returns link attribute .
     */
    protected String getExtraParameter(final String url)
    {
        String linkAttribute = "";

        if (url.contains("#"))
        {
            String[] strSpliter = url.split("#");

            if (strSpliter.length > 1)
            {
                if (strSpliter[1].contains("&"))
                {
                    String[] newStrSpliter = strSpliter[1].split("&",-1);
                    linkAttribute = newStrSpliter[0];
                }
                else
                {
                    linkAttribute = strSpliter[1];
                }
            }
        }

        return linkAttribute;
    }
    
	/**
	 * @param docGuid
	 * @return tocGuid TocGuid as a string for corresponding document guid.
	 * @throws EBookFormatException
	 */
	private String getTitleXMLTOCFilter(final String  docGuid) 
	{
		String tocGuid = "";
        if (docsGuidFile == null || !docsGuidFile.exists())
        {
        	throw new IllegalArgumentException(
        			"File passed into InternalLinkResolverFilter constructor must be a valid file.");
        }
		
			
		BufferedReader reader = null;
		try
		{
			//LOG.info("Reading in TOC anchor map file...");
			reader = new BufferedReader(new FileReader(docsGuidFile));
			String input = reader.readLine();
			while (input != null)
			{
				String[] line = input.split(",", -1);
				if (line[0].equals(docGuid))
				{
					String[] tocGuids = line[1].split("\\|");
					tocGuid=tocGuids[0];
					break;
				}
				input = reader.readLine();
				
			}
			
			if ("".equals(tocGuid))
			{
				String message = "Please verify that each document GUID in the following file has " +
							     "at least one TOC guid associated with it: " + 
				                  docsGuidFile.getAbsolutePath();
				LOG.error(message);					
			}
			
			LOG.info("Found a Toc Guid " + tocGuid + " For given Doc Guid " + docGuid);
		}
		catch(IOException e)
		{
			String message = "Could not read the DOC guid to TOC guid map file: " + 
					docsGuidFile.getAbsolutePath();
			LOG.error(message);			
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close DOC guid to TOC guid file reader.", e);
			}
		}
		
		return tocGuid;
	}
}
