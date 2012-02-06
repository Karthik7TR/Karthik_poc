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
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
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
	
	private static final String START_WRAPPER_TAG = "<Document>";
	private static final String END_WRAPPER_TAG = "</Document>";
	
	private DocMetadataService docMetadataService;
	
	private XSLTMapperService xsltMapperService;
	
	private FileHandlingHelper fileHandlingHelper;
	
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
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * @param titleID the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobID the identifier of the job currently running, used to lookup document metadata
     * 
     * @return The number of documents that were transformed
     * 
     * @throws EBookFormatException if an error occurs during the transformation process.
	 */
	@Override
	public int transformXMLDocuments(File xmlDir, File metaDir, File transDir, String titleID, Long jobID) 
			throws EBookFormatException 
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
        
        int docCount = 0;
        for(File xmlFile : xmlFiles)
        {
        	transformFile(xmlFile, metaDir, transDir, titleID, jobID);
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
	 * @param targetDir directory to which the ".transformed" files will be written
	 * @param titleId the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobId the identifier of the job currently running, used to lookup document metadata
     * 
	 * @throws EBookFormatException if Xalan processor runs into any error during the transformation process.
	 */
	final void transformFile(File xmlFile, File metadataDir, File targetDir, String titleId, Long jobId) 
			throws EBookFormatException
	{
		String fileNameUUID = xmlFile.getName().substring(0, xmlFile.getName().indexOf("."));
		
		File xslt = getXSLT(titleId, jobId, fileNameUUID);

		LOG.debug("Transforming XML file: " + xmlFile.getAbsolutePath());
        File tranFile = new File(targetDir, fileNameUUID + ".transformed");
        
        SequenceInputStream inStream1 = null;
        SequenceInputStream inStream2 = null;
        SequenceInputStream inStream3 = null;
		
        try
        {        	
        	inStream1 = new SequenceInputStream(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()),
        			new FileInputStream(xmlFile));
        	
        	inStream2 = new SequenceInputStream(inStream1, 
        			new FileInputStream(getMetadataFile(metadataDir, fileNameUUID)));
         	
        	inStream3 = new SequenceInputStream(inStream2,
        			new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
        	
	        Source xmlSource =
	                new StreamSource(inStream3);

	        Source xsltSource =
	                new StreamSource(xslt);
	        Result result =
	                new StreamResult(tranFile);
	 
	        // create an instance of TransformerFactory
	        TransformerFactory transFact =
	                TransformerFactory.newInstance();
	 
	        Transformer trans =
	                transFact.newTransformer(xsltSource);
	        
	        // set any Transformer properties
	        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        
	        // apply the XSLT transformations to the XML file
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
	 * Retrieves the XSLT file that should be used to transform the XML.
	 * 
	 * @param title title identifier to be used by the XSLT lookup service to determine which XSLT is to be applied
	 * @param job job identifier to be used by the XSLT lookup service to determine which XSLT is to be applied
	 * @param guid document guid which is used to lookup the document metadata needed for the XSLT lookup
	 * 
	 * @return the XSLT file to be used by the transformer
	 */
	protected File getXSLT(String title, Long job, String guid) throws EBookFormatException
	{
		File xsltDir = new File(new File("/nas", "Xslt"), "ContentTypes");
		String xsltName;
		String collection = "";
		String docType = "";
		
		//TODO: Remove hard coded XSLT name overrides that are used for the sample books.
		if (title.equalsIgnoreCase("crsample") || title.equalsIgnoreCase("scsample")
				|| title.equalsIgnoreCase("staticimage"))
		{
			xsltName = 
					"CodesStatutes.xsl";
		}
		else if(title.equalsIgnoreCase("analytical1"))
		{
			xsltName = 
					"AnalyticalJurs.xsl";
		}
		else if(title.equalsIgnoreCase("analytical2"))
		{
			xsltName = 
					"AnalyticalTreatisesAndAnnoCodes.xsl";
		}
		else
		{
			DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(title, Integer.parseInt(job.toString()), guid);

			if (docMetadata != null && StringUtils.isNotEmpty(docMetadata.getCollectionName()))
			{
				collection = docMetadata.getCollectionName();
				docType = docMetadata.getDocType();
			}
			else
			{
				String errMessage = "Could not retrieve document metadata for " + guid + " GUID under book " + title + 
						" title with " + job + " job id.";
	        	LOG.error(errMessage);
	        	throw new EBookFormatException(errMessage);
			}
			
			LOG.debug("Retrieved " + collection + " collection and " + docType + " doc type for " + guid + " document.");
			xsltName = xsltMapperService.getXSLT(collection, docType);
		}
		
		File xslt;
		if (xsltName != null)
		{
			xslt = new File(xsltDir, xsltName);
			
			if (!xslt.exists())
			{
				String errMessage = "Could not the following XSLT file on the file system: " + xslt.getAbsolutePath();
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
		
		LOG.debug("Using " + xsltName + " to transform " + guid + " document.");
        		
		return xslt;
	}
}
