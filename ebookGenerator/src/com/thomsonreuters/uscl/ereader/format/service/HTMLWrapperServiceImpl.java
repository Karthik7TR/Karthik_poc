/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * The HTMLWrapperService iterates through a directory of transformed raw HTML files and 
 * wraps the raw files with proper HTML header and container tags.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLWrapperServiceImpl implements HTMLWrapperService
{
	private static final Logger LOG = Logger.getLogger(TransformerServiceImpl.class);

	/**
     * Wraps all transformed files found in the passed in transformation directory and writes the
     * properly marked up HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param transDir the directory that contains all the intermediate generated HTML files generated
     * by the Transformer Service for this eBook.
     * @param htmlDir the target directory to which all the properly marked up HTML files will be written out to.
     * 
     * @throws EBookFormatException if an error occurs during the process.
	 */
	@Override
	public void addHTMLWrappers(File transDir, File htmlDir) throws EBookFormatException 
	{
        if (transDir == null || !transDir.isDirectory())
        {
        	throw new IllegalArgumentException("transDir must be a directory, not null or a regular file.");
        }
        
        //retrieve list of all transformed files that need HTML wrappers
		ArrayList<File> transformedFiles = new ArrayList<File>();
		getTransformedFiles(transformedFiles, transDir);
        
		if(!htmlDir.exists())
		{
			htmlDir.mkdirs();
		}
		
		LOG.info("Adding HTML containers around transformed files...");
		
		File htmlHeaderFile = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\Transformer\\HTMLHeader.txt");
		File htmlFooterFile = new File("C:\\COBALT_DW\\Warehouse\\eReader\\POC\\Transformer\\HTMLFooter.txt");
		
		for(File transFile : transformedFiles)
		{
			String fileName = transFile.getName();
			LOG.debug("Adding wrapper around: " + fileName);
			
			File output = new File(htmlDir, fileName.substring(0, fileName.indexOf(".")) + ".html");
			
			try
			{
				IOUtils.copy(new FileInputStream(htmlHeaderFile), new FileOutputStream(output));
				IOUtils.copy(new FileInputStream(transFile), new FileOutputStream(output, true));
				IOUtils.copy(new FileInputStream(htmlFooterFile), new FileOutputStream(output, true));	
			}
			catch(IOException ioe)
			{
				String errMessage = "Failed to add HTML contrainers around the following transformed file: " + fileName;
				LOG.error(errMessage);
				throw new EBookFormatException(errMessage, ioe);
			}
		}

		LOG.info("HTML containers successfully added to transformed files");
	}
	
	/**
	 * Builds up a transformed file list for the the specified directory.
	 * 
	 * @param fileList list to which all the found XML files will be appended to
	 * @param directory specifies where the XML files reside
	 * @throws EBookFormatException raised when no XML files have been found in the provided XML directory.
	 */
	final void getTransformedFiles(ArrayList<File> fileList, File directory) throws EBookFormatException
	{
		File[] files = directory.listFiles(new TransformedFilter());
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
	 * File filter that only accepts the custom intermediate files with the ".transformed" extension.
	 * 
     * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
	 */
	protected final class TransformedFilter implements FileFilter
	{
		private final String[] acceptedFileExtensions = new String[] {".transformed"};
		
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
