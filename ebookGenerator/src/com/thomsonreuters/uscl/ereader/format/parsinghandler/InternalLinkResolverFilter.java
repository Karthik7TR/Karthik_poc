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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.PaceMetadataService;
import com.thomsonreuters.uscl.ereader.util.CitationNormalizationRulesUtil;
import com.thomsonreuters.uscl.ereader.util.UrlParsingUtil;


/**
 * A SAX Filter that detects whether or not an anchor points to a resource included within an
 * eBook.<p>If there is a match, the URL is converted to ProView format &lt;a
 * href="er:#id"&gt;anchor text&lt;/a&gt;.</p>
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class InternalLinkResolverFilter extends XMLFilterImpl
{
    private static final Logger LOG = Logger.getLogger(InternalLinkResolverFilter.class);
    private static final String ANCHOR_ELEMENT = "a";
    private static final String HREF = "href";
    private static final String PROVIEW_ASSERT_REFERENCE_PREFIX = "er:#";
    private static final Long PUB_NOT_PRESENT = Long.MIN_VALUE;
    private DocumentMetadataAuthority documentMetadataAuthority;
    private File docsGuidFile;
    private PaceMetadataService paceMetadataService;
    private DocMetadataService docMetadataService;
    private Long jobId;

    public InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority)
    {
        if (documentMetadataAuthority == null)
        {
            throw new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority");
        }

        this.documentMetadataAuthority = documentMetadataAuthority;
    }

    public InternalLinkResolverFilter(
        final DocumentMetadataAuthority documentMetadataAuthority, final File docsGuidFile,
        final PaceMetadataService paceMetadataService, final DocMetadataService docMetadataService, final Long jobId)
    {
        if (documentMetadataAuthority == null)
        {
            throw new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority");
        }

        this.documentMetadataAuthority = documentMetadataAuthority;
        this.docsGuidFile = docsGuidFile;
        this.paceMetadataService = paceMetadataService;
        this.docMetadataService = docMetadataService;
        this.jobId = jobId;
    }

    @Override
    public void startElement(String uri, String localname, String qName, Attributes attributes)
        throws SAXException
    {
        String resourceUrl = attributes.getValue(HREF);

        if (
            StringUtils.isNotEmpty(resourceUrl) && resourceUrl.contains("http")
                    && ANCHOR_ELEMENT.equals(qName))
        {
            Map<String, String> urlContents = UrlParsingUtil.parseUrlContents(resourceUrl);
            Attributes resolvedAttributes = resolveResourceUrlReference(urlContents, attributes);
            super.startElement(uri, localname, qName, resolvedAttributes);
        }
        else
        {
            super.startElement(uri, localname, qName, attributes);
        }
    }

    /**
     * 
     * @param urlContents
     *
     * @return
     */
    private DocMetadata getDocMetadata(final Map<String, String> urlContents)
    {
        DocMetadata docMetadata = null;
        String cite = urlContents.get("cite");
        String documentUuid = urlContents.get("documentUuid");
        String serialNum = urlContents.get("serNum");

        if (StringUtils.isNotEmpty(cite))
        {
            String pubName = urlContents.get("pubNum");
            
            Long pubId = PUB_NOT_PRESENT;
            if (pubName != null && !pubName.trim().isEmpty())
            {
            	try
            	{
            		pubId =	Long.parseLong(pubName.trim());
            	}
            	catch (NumberFormatException nfe)
            	{
            		//not a valid serial number
            		LOG.debug("Encountered a pubName: " + pubName + " which is not a valid number.");
            	}
            }
            
            docMetadata = getNormalizedCiteDocMetadata(cite, pubId, jobId);
        }
        else if (StringUtils.isNotEmpty(documentUuid))
        {
            docMetadata = documentMetadataAuthority.getDocMetadataKeyedByDocumentUuid()
                                                   .get(documentUuid);
        }
        else if (StringUtils.isNotEmpty(serialNum))
        {
        	try
        	{
        		Long serNum = new Long(serialNum.trim());
        		
        		docMetadata = documentMetadataAuthority.getDocMetadataKeyedBySerialNumber().get(serNum);
        	}
        	catch (NumberFormatException nfe)
        	{
        		//not a valid serial number
        		LOG.debug("Encountered a serNum: " + serialNum + " which is not a valid number.");
        	}
        }

        return docMetadata;
    }

    /**
     * 
     * @param cite
     * @param pubId
     *
     * @return docMetadata DocumentMetadata
     */
    private DocMetadata getNormalizedCiteDocMetadata(String cite, Long pubId, Long jobId)
    {
       DocMetadata docMetadata = documentMetadataAuthority.getDocMetadataKeyedByCite().get(cite);

        if (docMetadata == null && !pubId.equals(PUB_NOT_PRESENT))
        {
            List<PaceMetadata> paceMetadataInfo =
                paceMetadataService.findAllPaceMetadataForPubCode(pubId);

            if ((paceMetadataInfo != null) && (paceMetadataInfo.size() > 0))
            {
                String stdPubName = paceMetadataInfo.get(0).getStdPubName();

                if (cite.contains(stdPubName))
                {
                    String pubName = paceMetadataInfo.get(0).getPublicationName();
                    cite = cite.replace(stdPubName, pubName);
                    cite = CitationNormalizationRulesUtil.applyNormalizationRules(cite);
                    docMetadata = documentMetadataAuthority.getDocMetadataKeyedByCite().get(cite);
                    
                    if (docMetadata == null)  {
                    	// look for a partial match with cite (this will fix multiple volumes) Bug #33426
                    	docMetadata = docMetadataService.findDocMetadataMapByPartialCiteMatchAndJobId(jobId, cite);
                    }
                    
                }
            }
        }
        else if (docMetadata == null && pubId.equals(PUB_NOT_PRESENT))
        {	
        	cite = CitationNormalizationRulesUtil.applyNormalizationRules(cite);
        	if(cite.startsWith("LK("))
        	{
        		docMetadata = getRefsAnnosPage(cite);
        	}
        }

        return docMetadata;
    }

    /**
     * 
     * @param docGuid
     *
     * @return tocGuid TocGuid as a string for corresponding document guid.
     *
     * @throws EBookFormatException
     */
    private String getTitleXMLTOCFilter(final String docGuid)
    {
        String tocGuid = "";

        if ((docsGuidFile == null) || !docsGuidFile.exists())
        {
            throw new IllegalArgumentException(
                "File passed into InternalLinkResolverFilter constructor must be a valid file.");
        }

        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(docsGuidFile));

            String input = reader.readLine();

            while (input != null)
            {
                String[] line = input.split(",", -1);

                if (line[0].equals(docGuid))
                {
                    String[] tocGuids = line[1].split("\\|");
                    tocGuid = tocGuids[0];

                    break;
                }

                input = reader.readLine();
            }

            if ("".equals(tocGuid))
            {
                String message =
                    "Please verify that each document GUID in the following file has "
                    + "at least one TOC guid associated with it: " + docsGuidFile.getAbsolutePath();
                LOG.error(message);
            }
        }
        catch (IOException e)
        {
            String message =
                "Could not read the DOC guid to TOC guid map file: "
                + docsGuidFile.getAbsolutePath();
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

    /**
     * Resolves a normalized cite reference to a WLN resource to an internal book
     * reference, provided it exists in the normalizedCiteAuthority.
     *
     * @param urlContents the normalizedCite to match against the keys in the
     *        normalizedCiteAuthority.
     * @param attributes
     *
     * @return the updated URL in ProView format.
     */
    private Attributes resolveResourceUrlReference(
        final Map<String, String> urlContents, Attributes attributes)
        throws SAXException
    {
        DocMetadata docMetadata = getDocMetadata(urlContents);

        AttributesImpl resolvedAttributes = new AttributesImpl(attributes);

        if (docMetadata == null)
        {
            return resolvedAttributes;
        }

        String ebookResourceIdentifier = docMetadata.getProViewId();

        String reference = urlContents.get("reference");

        if (StringUtils.isNotEmpty(reference))
        {
            ebookResourceIdentifier = ebookResourceIdentifier + "/" + reference;
        }
        else
        {
            String tocGuid = getTitleXMLTOCFilter(docMetadata.getDocUuid());

            if (StringUtils.isEmpty(tocGuid))
            {
                throw new SAXException(
                    "Could not find TOC guid for " + docMetadata.getDocUuid() + " document.");
            }

            ebookResourceIdentifier = ebookResourceIdentifier + "/" + tocGuid;
        }

        int anchorReferenceIndex = resolvedAttributes.getIndex(HREF);
        resolvedAttributes.setValue(
            anchorReferenceIndex, PROVIEW_ASSERT_REFERENCE_PREFIX + ebookResourceIdentifier); //add the new value

        return resolvedAttributes;
    }
    
    /**
	 * Attempts to locate the page of a Refs and Annos citations that is in one of the following formats: 
	 * lk(TXPRD)+lk(TXPRR)+lk(TXAGD)+lk(TXALD)+lk(TXBCD)+...
	 * lk(TXPRD) lk(TXPRR) lk(TXAGD) lk(TXALD) lk(TXBCD)
     * 
     * @param cite Refs and Annos citation
     * 
     * @return null if document is not identified in book otherwise the document metadata object of the Refs and Annos page.
     */
    private DocMetadata getRefsAnnosPage(String cite)
    {
    	DocMetadata docMetadata = null;

    	String[] citations = cite.split("LK\\(");
    	
    	//try and find the correct reference page from the list of citations
    	for (String citation : citations)
    	{
    		citation = citation.replace(")", "").replace("+", "").trim();
    		
    		if (citation.endsWith("R"))
    		{	
    			//Only look at the citations that end with R, presumably stands for References
        		docMetadata = documentMetadataAuthority.getDocMetadataKeyedByCite().get(citation);
        		
        		if (docMetadata != null)
        		{
        			return docMetadata;
        		}
    		}
    	}
    	
    	//if a reference page cannot be located, locate to the first found document in the cite list
    	if (docMetadata == null)
    	{
        	for (String citation : citations)
        	{
        		citation = citation.replace(")", "").replace("+", "").trim();
        		
        		docMetadata = documentMetadataAuthority.getDocMetadataKeyedByCite().get(citation);
        		
        		if (docMetadata != null)
        		{
        			return docMetadata;
        		}
        	}
    	}
    	
    	return docMetadata;
    }
}
