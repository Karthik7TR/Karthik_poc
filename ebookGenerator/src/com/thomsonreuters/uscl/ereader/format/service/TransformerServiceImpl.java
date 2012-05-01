/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XSLIncludeResolver;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * The TransformerServiceImpl iterates through a directory of XML files, retrieves the appropriate XSLT stylesheets, 
 * compiles them and produces intermediate HTML files that do not yet have all the proper HTML document wrappers 
 * and ProView mark up. 
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TransformerServiceImpl implements TransformerService
{
	private static final Logger LOG = Logger.getLogger(TransformerServiceImpl.class);
	
	private static final String START_WRAPPER_TAG = "<Document>" ;
	private static final String END_WRAPPER_TAG = "</Document>";
	
	private DocMetadataService docMetadataService;
	
	private GenerateDocumentDataBlockService generateDocumentDataBlockService;
	
	private XSLTMapperService xsltMapperService;
	
	private FileHandlingHelper fileHandlingHelper;
	
	@Required
	public void setGenerateDocumentDataBlockService(
			GenerateDocumentDataBlockService generateDocumentDataBlockService) {
		this.generateDocumentDataBlockService = generateDocumentDataBlockService;
	}

	public void setdocMetadataService(DocMetadataService docMetadataService) 
	{
		this.docMetadataService = docMetadataService;
	}
	
	public void setxsltMapperService(XSLTMapperService xsltMapperService) 
	{
		this.xsltMapperService = xsltMapperService;
	}
	
	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}
	
	/**
     * Transforms all XML files found in the passed in XML directory and writes the
     * transformed HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param xmlDir the directory that contains all the Novus extracted XML files for this eBook.
     * @param metaDir the directory that contains all the Novus document metadata files for this eBook.
     * @param imgMetaDir the directory that contains all the ImageMetadata built files for this eBook.
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * @param titleID the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobID the identifier of the job currently running, used to lookup document metadata
     * 
     * @return The number of documents that were transformed
     * 
     * @throws EBookFormatException if an error occurs during the transformation process.
	 */
	@Override
	public int transformXMLDocuments(final File xmlDir, final File metaDir, final File imgMetaDir,
			final File transDir, final String titleID, final Long jobID) throws EBookFormatException 
	{
        if (xmlDir == null || !xmlDir.isDirectory())
        {
        	throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }
        
		if(!transDir.exists())
		{
			transDir.mkdirs();
		}

        LOG.info("Transforming XML files from the following XML directory: " + xmlDir.getAbsolutePath());
        
        List<File> xmlFiles = new ArrayList<File>();
        
        try
        {
        	fileHandlingHelper.getFileList(xmlDir, xmlFiles);
        }
        catch(FileNotFoundException e)
        {
        	String errMessage = "No XML files were found in specified directory. " +
					"Please verify that the correct XML path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
        
        Map<String, Transformer> xsltCache = new HashMap<String, Transformer>();
        
        int docCount = 0;
        for(File xmlFile : xmlFiles)
        {
        	transformFile(xmlFile, metaDir, imgMetaDir, transDir, titleID, jobID, xsltCache);
        	docCount++;
        }
        LOG.info("Transformed all XML files");
        
        return docCount;
	}
	
	/**
	 * Based on the file name, retrieves the appropriate XSLT file name and
	 * transforms the passed in XML file using it.
	 * 
	 * @param xmlFile XML file to be transformed
	 * @param metadataDir directory that contains all the metadata files
     * @param imgMetadataDir the directory that contains all the ImageMetadata built files for this eBook.
	 * @param targetDir directory to which the ".transformed" files will be written
	 * @param titleId the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobId the identifier of the job currently running, used to lookup document metadata
     * 
	 * @throws EBookFormatException if Xalan processor runs into any error during the transformation process.
	 */
	final void transformFile(File xmlFile, File metadataDir, File imgMetadataDir, File targetDir, String titleId, 
			Long jobId, Map<String, Transformer> stylesheetCache) throws EBookFormatException
	{
		String fileNameUUID = xmlFile.getName().substring(0, xmlFile.getName().indexOf("."));
		
		String[] metadata = new String[2];
		
		lookupCollectionDocType(titleId, jobId, fileNameUUID, metadata);

		File xslt = getXSLT(metadata[0], metadata[1]);

		//LOG.debug("Transforming XML file: " + xmlFile.getAbsolutePath());
        File tranFile = new File(targetDir, fileNameUUID + ".transformed");
        
        SequenceInputStream inStream1 = null;
        SequenceInputStream inStream2 = null;
        SequenceInputStream inStream3 = null;
        SequenceInputStream inStream4 = null;
        SequenceInputStream inStream0 = null;
        
        Transformer trans = null;
		
        try
        {   
        	InputStream documentDataStream =   generateDocumentDataBlockService.getDocumentDataBlockAsStream(titleId, jobId, fileNameUUID);
        	
        	inStream0 = new SequenceInputStream(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()),
        			documentDataStream);
        	
        	inStream1 = new SequenceInputStream(inStream0,
        			new FileInputStream(xmlFile));
        	
        	inStream2 = new SequenceInputStream(inStream1, 
        			new FileInputStream(getMetadataFile(metadataDir, fileNameUUID)));
        	
        	inStream3 = new SequenceInputStream(inStream2,
        			new FileInputStream(getImageMetadataFile(imgMetadataDir, fileNameUUID)));
         	
        	inStream4 = new SequenceInputStream(inStream3,
        			new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
        	
	        Source xmlSource =
	                new StreamSource(inStream4);    
	       
	        if (!stylesheetCache.containsKey(xslt.getAbsolutePath()))
	        {
	        	trans = createTransformer(xslt, stylesheetCache);
	        }
	        else
	        {
	        	trans = stylesheetCache.get(xslt.getAbsolutePath());
	        }
	        
	        setDocLvlTransParams(trans, metadata[0]);
	        
	        Result result =
	                new StreamResult(tranFile);
	        
	        trans.transform(xmlSource, result);

	        LOG.debug("Successfully transformed: " + xmlFile.getAbsolutePath());
        }
        catch (TransformerException te)
        {
        	String errMessage = "Encountered transformation issues trying to transform " + xmlFile.getName() + 
        			" xml file using " + xslt.getName() + " xslt file.";
        	LOG.error(errMessage, te);
        	throw new EBookFormatException(errMessage, te);
        }
        catch (FileNotFoundException e)
        {
        	String errMessage = "Could not find the following xml file: " + xmlFile.getName();
        	LOG.error(errMessage, e);
        	throw new EBookFormatException(errMessage, e);
        }
        finally
        {
        	try
        	{
	        	if (inStream3 != null)
	        	{
	        		inStream3.close();
	        	}
	        	if (inStream2 != null)
	        	{
	        		inStream2.close();
	        	}
	        	if (inStream1 != null)
	        	{
	        		inStream1.close();
	        	}
        	}
        	catch (IOException e)
        	{
        		LOG.error("Unable to close files related to the " + xmlFile.getAbsolutePath() 
        				+ " file transformation.", e);
        	}
        }
	}
	
	/**
	 * Finds and returns the file that contains the ImageMetadata block that should be appended
	 * to the transforming document that is identified by the passed in document guid.
	 * 
	 * @param imgMetaDir directory that contains all the generated ImageMetadata files
	 * @param docGuid guid of the document being transformed
	 * 
	 * @return File that contains the ImageMetadata for the passed in document guid
	 * @throws EBookFormatException if no ImageMetadata is found for the passed in document guid
	 */
	protected File getImageMetadataFile(File imgMetaDir, String docGuid)
		throws EBookFormatException
	{
		File imgMetadata = null;
		
		for (File aFile : imgMetaDir.listFiles())
		{
			if (aFile.getName().equals(docGuid + ".imgMeta"))
			{
				imgMetadata = aFile;
			}
		}
		
		if (imgMetadata==null || !imgMetadata.exists())
		{
			String errMessage = "Could not find the ImageMetadata file for  " + docGuid + " GUID in the " 
					+ imgMetaDir.getAbsolutePath() + " directory.";
        	LOG.error(errMessage);
        	throw new EBookFormatException(errMessage);
		}
		
		return imgMetadata;
	}
	
	/**
	 * Finds and returns the document metadata file associated to the passed in document guid.
	 * 
	 * @param metaDir directory containing all the metadata files
	 * @param docGuid GUID of the document
	 * @return File representing the metadata associated with the passed in document GUID
	 * @throws EBookFormatException no metadata file was found
	 */
	protected File getMetadataFile(File metaDir, String docGuid)
		throws EBookFormatException
	{
		File metadataFile = null;
		
		for (File aFile : metaDir.listFiles())
		{
			if (aFile.getName().endsWith(docGuid + ".xml"))
			{
				 metadataFile = aFile;
			}
		}
		
		if (metadataFile==null || !metadataFile.exists())
		{
			String errMessage = "Could not find the document metadata file for  " + docGuid + " GUID in the " 
					+ metaDir + " metadata directory.";
        	LOG.error(errMessage);
        	throw new EBookFormatException(errMessage);
		}
		
		return metadataFile;
	}
	
	/**
	 * Creates, configures and caches a transformer for the passed in XSLT file.
	 * 
	 * @param transformer transformer to be created and configured
	 * @param xslt stylesheet for which the transformer will be created
	 * @param xsltCache cache of stylesheets to be updated with new transformer
	 * 
	 * @return configured transformer
	 * @throws EBookFormatException if the transformer could not be configured correctly
	 */
	protected Transformer createTransformer(File xslt, Map<String, Transformer> xsltCache)
		throws EBookFormatException
	{
		try
		{
	        Source xsltSource = new StreamSource(xslt);
	    	
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        
	        XSLIncludeResolver resolver = new XSLIncludeResolver();
	        transFact.setURIResolver(resolver);
	 
	        Transformer transformer = transFact.newTransformer(xsltSource);
	        
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setParameter("IsPersisted", true);
	        transformer.setParameter("UniqueIdForBlobs", UUID.randomUUID().toString());
	        transformer.setParameter("IsMobile", false);
	        transformer.setParameter("LinkUnderline", false);
	        transformer.setParameter("StatutoryTextOnly", false);
	        transformer.setParameter("ListItemIdentifier", "Target");
	        transformer.setParameter("SpecialRequestSourceParam", "cblt1.0");
	        transformer.setParameter("SpecialVersionParam", "3.0");
	        transformer.setParameter("Target", "_top");
	        transformer.setParameter("DisplayOriginalImageLink", true);
//	        transformer.setParameter("LinkColor", "");
//	        transformer.setParameter("FontSize", "");
	        //None of the highlighting parameters are set, example: 
	        //DisplayTermHighlighting, IsSearched, Quotes, SourceSerial
//	        transformer.setParameter("HasDocketOrdersAccess", "");
//	        transformer.setParameter("DisplayDocketUpdateLink", false);
//	        transformer.setParameter("DocketIsSlowCourt", "");
//	        transformer.setParameter("DisplayFormAssembleLink", true);
//	        transformer.setParameter("AllowLinkDragAndDrop", true);
//	        transformer.setParameter("HasCalenderingInformation", false);
//	        transformer.setParameter("EasyEditMode", false);
//	        transformer.setParameter("DisplayEasyEditLink", true);
//	        transformer.setParameter("DisplayLinksInDocument", true);
//	        transformer.setParameter("IncludeCopyWithRefLinks", "");
//	        transformer.setParameter("DeliveryMode", "");
//	        transformer.setParameter("DeliveryFormat", "");
//	        transformer.setParameter("DualColumnMode", false);
//	        transformer.setParameter("DisplayOnlyPagesWithSearchTerms", "");
//	        transformer.setParameter("HeadnoteDisplayOption", "");
//	        transformer.setParameter("mediaPageWidth", "925");
//	        transformer.setParameter("mediaPageHeight", "1 div 0");
//	        transformer.setParameter("EffectiveStartDate", "");
//	        transformer.setParameter("EffectiveEndDate", "");
//	        transformer.setParameter("contextualInfo", "");
	        //None of the search within parameters are set, example:
	        //PrimaryTermsWordset, SecondaryTermsWordset, SearchWithinTermsWordset
	        
	        SimpleDateFormat format = new SimpleDateFormat("yyyy");
	        String year = format.format(new Date());
	        transformer.setParameter("currentYear", year);
//	        transformer.setParameter("endOfDocumentCopyrightText", "Thomson Reuters. No claim to original " +
//	        		"U.S. Government Works."); //Request was made to remove the copyright text from the End of Doc section
	        
	        xsltCache.put(xslt.getAbsolutePath(), transformer);
	        
	        return transformer;
		}
		catch (TransformerConfigurationException e)
		{
			String errMessage = "Encountered transformer configuration issues with " + xslt.getAbsolutePath() + 
        			" xslt file.";
        	LOG.error(errMessage, e);
        	throw new EBookFormatException(errMessage, e);
		}
	}
	
	/**
	 * Sets all the document specific transformation parameters on the passed in transformer.
	 * 
	 * @param transformer transformer to be configured
	 * @param collection novus collection of document being processed
	 */
	protected void setDocLvlTransParams(Transformer transformer, String collection)
	{
		List<String> royaltyIdCollection = Arrays.asList("w_3rd_millann", "w_3rd_millpol", 
				"w_lt_td_motions", "w_lt_td_ew", "w_lt_td_filings");
		
		if (royaltyIdCollection.contains(collection))
		{
			transformer.setParameter("UseBlobRoyaltyId", true);
		}
	}
	
	/**
	 * Retrieves the XSLT file that should be used to transform the XML.
	 * 
	 * @param collection the novus collection of the document to be transformed
	 * @param docType the document type as found in the metadata for the doucment to be transformed
	 * 
	 * @return the XSLT file to be used by the transformer
	 */
	protected File getXSLT(String collection, String docType) 
			throws EBookFormatException
	{
		File xsltDir = new File("/apps/eBookBuilder/staticContent/ContentTypes");
		String xsltName;		
		
		File xslt = null;
		xsltName = xsltMapperService.getXSLT(collection, docType);
		
		if (xsltName != null)
		{
			xslt = new File(xsltDir, xsltName);
			
			if (!xslt.exists())
			{
				String errMessage = "Could not find the following XSLT file on the file system: " + 
						xslt.getAbsolutePath();
	        	LOG.error(errMessage);
	        	throw new EBookFormatException(errMessage);
			}
		}
		else
		{
			String errMessage = "Could not retrieve xslt name through XSLTMapperService. Please make sure " +
					"an entry for " + collection + " collection and " + docType + " doc type exist in the " +
					"XSTL_MAPPER table on the EBook database.";
        	LOG.error(errMessage);
        	throw new EBookFormatException(errMessage);
		}

		LOG.debug("Using " + xsltName + " to transform document.");
		
		return xslt;
	}
	
	/**
	 * Looks up the collection and document type for the document represented by the GUID with the run
	 * identified by the title and job composite key.
	 * 
	 * @param title title identifier to be used by the XSLT lookup service to determine which XSLT is to be applied
	 * @param jobInstanceId job identifier to be used by the XSLT lookup service to determine which XSLT is to be applied
	 * @param guid document guid which is used to lookup the document metadata needed for the XSLT lookup
	 * @param metadata used to pass back any retrieved data for the passed in GUID
	 */
	public void lookupCollectionDocType(String title, Long jobInstanceId, String guid, String[] metadata)
		throws EBookFormatException
	{
		DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(
				title, jobInstanceId, guid);
		
		String collection;
		String docType;

		if (docMetadata != null && StringUtils.isNotEmpty(docMetadata.getCollectionName()))
		{
			collection = docMetadata.getCollectionName();
			docType = docMetadata.getDocType();
		}
		else
		{
			String errMessage = "Could not retrieve document metadata for " + guid + " GUID under book " + title + 
					" title with " + jobInstanceId + " job id.";
        	LOG.error(errMessage);
        	throw new EBookFormatException(errMessage);
		}
		
		metadata[0] = collection;
		metadata[1] = docType;
		
		LOG.debug("Retrieved " + collection + " collection and " + docType + " doc type for " + guid + " document.");
	}
}
