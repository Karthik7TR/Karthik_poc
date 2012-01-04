/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

/**
 * The TransformerServiceImpl iterates through a directory of XML files, retrieves the appropriate XSLT stylesheets, 
 * compiles them and produces intermediate HTML files that do not yet have all the proper HTML document wrappers 
 * and ProView mark up. 
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Service("TransformerService")
public class TransformerServiceImpl implements TransformerService
{
	private static final Logger LOG = Logger.getLogger(TransformerServiceImpl.class);
	
	@Autowired
	private DocMetadataService docMetadataService;
	
	@Autowired
	private XSLTMapperService xsltMapperService;
	
	public void setdocMetadataService(DocMetadataService docMetadataService) 
	{
		this.docMetadataService = docMetadataService;
	}
	
	public void setxsltMapperService(XSLTMapperService xsltMapperService) 
	{
		this.xsltMapperService = xsltMapperService;
	}
	
	/**
     * Transforms all XML files found in the passed in XML directory and writes the
     * transformed HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param xmlDir the directory that contains all the Novus extracted XML files for this eBook.
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * @param titleID the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobID the identifier of the job currently running, used to lookup document metadata
     * 
     * @return The number of documents that were transformed
     * 
     * @throws EBookFormatException if an error occurs during the transformation process.
	 */
	@Override
	public int transformXMLDocuments(File xmlDir, File transDir, String titleID, Long jobID) throws EBookFormatException 
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
        
        ArrayList<File> xmlFiles = new ArrayList<File>();
        getXMLFiles(xmlFiles, xmlDir);
        
        int docCount = 0;
        for(File xmlFile : xmlFiles)
        {
        	transformFile(xmlFile, transDir, titleID, jobID);
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
	 * @param targetDir directory to which the ".transformed" files will be written
	 * @param titleId the identifier of book currently being published, used to lookup appropriate document metadata
     * @param jobId the identifier of the job currently running, used to lookup document metadata
     * 
	 * @throws EBookFormatException if Xalan processor runs into any error during the transformation process.
	 */
	final void transformFile(File xmlFile, File targetDir, String titleId, Long jobId) throws EBookFormatException
	{
		File xsltDir = new File(new File("C:\\nas", "Xslt"), "ContentTypes");
		String fileNameUUID = xmlFile.getName().substring(0, xmlFile.getName().indexOf("."));
		
		//TODO: Dynamically retrieve XSLT using XSLT retrieval service
		DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, Integer.parseInt(jobId.toString()), fileNameUUID);

		File xslt = new File(xsltDir, xsltMapperService.getXSLT(docMetadata.getCollectionName(), docMetadata.getDocType()));
        
        //File xslt = new File(xsltDir, "SimpleContentBlocks.xsl");
        //File xslt = new File(xsltDir, "CodesStatutes.xsl");
        //File xslt = new File(xsltDir, "AnalyticalJurs.xsl");
		//File xslt = new File(xsltDir, "AnalyticalTreatisesAndAnnoCodes.xsl");

		LOG.debug("Transforming XML file: " + xmlFile.getAbsolutePath());
        File tranFile = new File(targetDir, fileNameUUID + ".transformed");
		
        try
        {        	
	        Source xmlSource =
	                new StreamSource(xmlFile);
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
        catch(TransformerException te)
        {
        	String errMessage = "Encountered transformation issues trying to transform " + xmlFile.getName() + 
        			" xml file using " + xslt.getName() + " xslt file.";
        	LOG.error(errMessage, te);
        	throw new EBookFormatException(errMessage, te);
        }
	}

	/**
	 * Builds up a XML file list for the the specified directory.
	 * 
	 * @param fileList list to which all the found XML files will be appended to
	 * @param directory specifies where the XML files reside
	 * @throws EBookFormatException raised when no XML files have been found in the provided XML directory.
	 */
	final void getXMLFiles(ArrayList<File> fileList, File directory) throws EBookFormatException
	{
		File[] files = directory.listFiles(new XMLFilter());
		fileList.addAll(Arrays.asList(files));
		if(fileList.size() == 0)
		{
			String errMessage = "No XML files were found in specified directory. " +
					"Please verify that the correct XML path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage);
		}
	}
	
	/**
	 * File filter that only accepts XML files, files that end with ".xml".
	 * 
     * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
	 */
	protected final class XMLFilter implements FileFilter
	{
		private final String[] acceptedFileExtensions = new String[] {".xml"};
		
		@Override
		public boolean accept(File file) 
		{
			for (String extension : acceptedFileExtensions)
			{
				if (file.isFile() && file.getName().toLowerCase().endsWith(extension))
				{
					return true;
				}
			}
			
			return false;
		}
	}
}
