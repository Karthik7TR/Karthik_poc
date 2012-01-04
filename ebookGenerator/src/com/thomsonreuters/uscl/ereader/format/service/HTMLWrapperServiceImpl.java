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
import java.io.InputStream;
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
     * @return The number of documents that had wrappers added
     * 
     * @throws EBookFormatException if an error occurs during the process.
	 */
	@Override
	public int addHTMLWrappers(File transDir, File htmlDir) throws EBookFormatException 
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
				
		int numDocs = 0;
		for(File transFile : transformedFiles)
		{
			addHTMLWrapperToFile(transFile, htmlDir);
			numDocs++;
		}

		LOG.info("HTML containers successfully added to transformed files");
		return numDocs;
	}
	
	/**
	 * Takes in a .tranformed file and wraps it with the appropriate header and footers, the resulting file is
	 * written to the specified HTML directory.
	 * 
	 * @param transformedFile source file that will be wrapped with appropriate header and footer
	 * @param htmlDir target directory the newly created file with the wrappers will be written to
	 * @throws EBookFormatException thrown if any IO exceptions are encountered
	 */
	final void addHTMLWrapperToFile(File transformedFile, File htmlDir) throws EBookFormatException
	{
		String fileName = transformedFile.getName();
		LOG.debug("Adding wrapper around: " + fileName);
		
		File output = new File(htmlDir, fileName.substring(0, fileName.indexOf(".")) + ".html");
		
		try
		{
			FileOutputStream outputStream = new FileOutputStream(output, true);
			
			InputStream headerStream = getClass().getResourceAsStream("/StaticFiles/HTMLHeader.txt");
			IOUtils.copy(headerStream, outputStream);
			headerStream.close();
			
			InputStream transFileStream = new FileInputStream(transformedFile);
			IOUtils.copy(transFileStream, outputStream);
			transFileStream.close();
			
			InputStream footerStream = getClass().getResourceAsStream("/StaticFiles/HTMLFooter.txt");
			IOUtils.copy(footerStream, outputStream);
			footerStream.close();
			
			outputStream.close();
		}
		catch(IOException ioe)
		{
			String errMessage = "Failed to add HTML contrainers around the following transformed file: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, ioe);
		}
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
			String errMessage = "No transformed files were found in specified directory. " +
					"Please verify that the correct path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage);
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
