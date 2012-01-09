/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * The HTMLWrapperService iterates through a directory of transformed raw HTML files and 
 * wraps the raw files with proper HTML header and container tags.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLWrapperServiceImpl implements HTMLWrapperService
{
	private static final Logger LOG = Logger.getLogger(TransformerServiceImpl.class);
	
	private FileHandlingHelper fileHandlingHelper;
	
	public void setfileHandler(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}

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
		
		try
		{
			fileHandlingHelper.getFileList(transDir, transformedFiles);
		}
        catch(FileNotFoundException e)
        {
        	String errMessage = "No transformed files were found in specified directory. " +
					"Please verify that the correct transformed path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
        
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
		
		FileOutputStream outputStream = null;
		InputStream headerStream = null;
		InputStream transFileStream = null;
		InputStream footerStream = null;
		try
		{
			outputStream = new FileOutputStream(output, true);
			
			headerStream = getClass().getResourceAsStream("/StaticFiles/HTMLHeader.txt");
			IOUtils.copy(headerStream, outputStream);
			
			transFileStream = new FileInputStream(transformedFile);
			IOUtils.copy(transFileStream, outputStream);
			
			footerStream = getClass().getResourceAsStream("/StaticFiles/HTMLFooter.txt");
			IOUtils.copy(footerStream, outputStream);
		}
		catch(IOException ioe)
		{
			String errMessage = "Failed to add HTML contrainers around the following transformed file: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, ioe);
		}
		finally
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.close();
				}
				if (headerStream != null)
				{
					headerStream.close();
				}
				if (transFileStream != null)
				{
					transFileStream.close();
				}
				if (footerStream != null)
				{
					footerStream.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close I/O streams.", e);
			}
		}
	}
}
