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

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

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
	
	/**
     * Transforms all XML files found in the passed in XML directory and writes the
     * transformed HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param xmlDir the directory that contains all the Novus extracted XML files for this eBook.
     * @param transDir the target directory to which all the intermediate HTML files will be written out to.
     * 
     * @throws EBookFormatException if an error occurs during the transformation process.
	 */
	@Override
	public void transformXMLDocuments(File xmlDir, File transDir) throws EBookFormatException 
	{
        if (xmlDir == null || !xmlDir.isDirectory())
        {
        	throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }
        
		if(!transDir.exists())
		{
			transDir.mkdirs();
		}
        
        //File xslt = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\XML\\Xslt\\ContentBlocks\\SimpleContentBlocks.xsl");
        //File xslt = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\Transformer\\Xslt\\ContentTypes\\CodesStatutes.xsl");
        //File xslt = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\Transformer\\Xslt\\ContentTypes\\AnalyticalJurs.xsl");
        File xslt = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\Transformer\\Xslt\\ContentTypes\\AnalyticalTreatisesAndAnnoCodes.xsl");

        LOG.info("Transforming files using XSLT: " + xslt.getAbsolutePath() +
        			"\n\tfrom the following XML directory: " + xmlDir.getAbsolutePath());
        
        ArrayList<File> xmlFiles = new ArrayList<File>();
        getXMLFiles(xmlFiles, xmlDir);
        
        for(File xmlFile : xmlFiles)
        {
        	LOG.debug("Transforming XML file: " + xmlFile.getAbsolutePath());
            File tranFile = new File(transDir, xmlFile.getName().substring(0, xmlFile.getName().indexOf(".")) + ".transformed");
    		
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
        LOG.info("Transformed all XML files");
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
			throw new EBookFormatException(errMessage, null);
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
