/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;

/**
 * EBookAssemblyServiceImpl is responsible for creating a valid compressed tarball from whichever directory is passed as input to the assembleEBook method.
 * 
 * <p>This class makes use of recursion to create the archive and cannot cope with circular directory structures. 
 * This class is not responsible for determining that the eBook format, which ProView expects, is correct.</p> 
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class EBookAssemblyServiceImpl implements EBookAssemblyService
{
	private static final Logger LOG = Logger.getLogger(EBookAssemblyServiceImpl.class);
	
	/**
	 * Assembles an eBook given an eBookDirectory where the files reside and a file to stream the output to.
	 * 
	 * @param eBookDirectory the input directory that contains the eBook structure (artwork, assets, documents directories and title.xml)
	 * @param eBook the title that is created as a result of the process.
	 * @throws EBookAssemblyException if something unexpected happens during the process.
	 */
	@Override
	public final void assembleEBook(final File eBookDirectory, final File eBook) throws EBookAssemblyException
	{
        if (eBookDirectory == null || !eBookDirectory.isDirectory())
        {
        	throw new IllegalArgumentException("eBookDirectory must be a directory, not null or a regular file.");
        }
        if (eBook == null || eBook.isDirectory())
        {
        	throw new IllegalArgumentException("eBook must be a regular file, not null or a directory.");
        }
              
        LOG.debug("Assembling eBook using the input directory: " + eBookDirectory.getAbsolutePath());
        
        TarOutputStream tarOutputStream = null;
        
        try
        {                 
        	tarOutputStream = new TarOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(eBook))));
        	tarOutputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
        	
            recursivelyTarDirectory(tarOutputStream, eBookDirectory.getAbsolutePath(), "");
            tarOutputStream.flush();
        }
        catch (FileNotFoundException e)
        {
            String message = "Destination file is inaccesable or does not exist. Is write permission set on " + eBook.getName() + "?";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
        catch (IOException e)
        {
            String message =
                "Failed to flush the TarOutputStream to disk.  Is the disk full?";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
        finally
        {
        	IOUtils.closeQuietly(tarOutputStream);
        }
		
	}
	/**
	 * gets list of documents in Document folder
	 * @param contentFolderPath
	 * @return
	 */
	@Override
	public long getDocumentCount(final String contentFolderPath)
	{
		
		long docCount = 0; 
		File contentDir = new File(contentFolderPath); 
		File fileList[] = contentDir.listFiles();
		docCount = fileList.length;
		return docCount; 
	}
	/**
	 * gets back largest content file size for passed in file path and content type (file Extension, may contain multiple csv extensions)
	 */
	@Override
	public long getLargestContent(final String contentFolderPath,String fileExtension)
	{ 
		String[] extensions;
		extensions = fileExtension.split(",");
						
		long largestFileSize = 0; 
		File contentDri = new File(contentFolderPath); 
		File fileList[] = contentDri.listFiles();
		for (File file : fileList) {						//every file in directory
			for (String extend : extensions){				//every extension input
				if(file.getAbsolutePath().endsWith(extend)){
					if(largestFileSize < file.length()){
						largestFileSize = file.length();
					}
					break;									//file extension identified, no need to check more
				}
			}
		}
	
		return largestFileSize; 
	}
	
    /**
     * Recursively searches through a directory and creates a tar entry for each file and
     * folder.
     *
     * @param tarOutputStream - the TarOutputStream where each file will be streamed to.
     * @param path - the path to the directory to be added to the archive
     * @param base - the path prefix to be used for each entry within the archive
     *
     * @throws EBookAssemblyException
     */
    final void recursivelyTarDirectory(final TarOutputStream tarOutputStream, final String path, final String base)
        throws EBookAssemblyException
    {
        File targetFile = new File(path);
        String entryName = base + targetFile.getName();
        
        addTarEntry(tarOutputStream, targetFile, entryName);

        if (targetFile.isFile())
        {
            writeTarFileToArchive(tarOutputStream, targetFile);
        }
        else
        {
            try
            {
            	tarOutputStream.closeEntry();
            }
            catch (IOException e)
            {
                String message = "error while attempting to flush TarOutputStream";
                LOG.error(message, e);
                
                throw new EBookAssemblyException(message, e);
            }

            File[] children = targetFile.listFiles();

            if (children != null)
            {
                for (File file : children)
                {
                	recursivelyTarDirectory(tarOutputStream, file.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }

    /**
     * Copies the contents of a file into the stream that represents the TAR file.
     * 
     * @param tarOutputStream the TarOutputStream that will be written to disk (at some point).
     * @param targetFile the file to be added to the archive.
     * 
     * @throws EBookAssemblyException if an underlying FileNotFoundException or IOException occurs.
     */
	final void writeTarFileToArchive(final TarOutputStream tarOutputStream, final File targetFile) throws EBookAssemblyException
	{
		try
		{
		    IOUtils.copy(new FileInputStream(targetFile), tarOutputStream);
		    tarOutputStream.closeEntry();
		}
		catch (FileNotFoundException e)
		{
		    String message =
		        "Could not write contents of file to TarOutputStream. Does the input file exist at the specified location? " +
		        "Are read permissions enabled on the file: " + targetFile.getName() + "?";
		    LOG.error(message, e);
		    throw new EBookAssemblyException(message, e);
		}
		catch (IOException e)
		{
		    String message = "Could not close entry in tar file.";
		    LOG.error(message, e);
		    throw new EBookAssemblyException(message, e);
		}
	}

	/**
	 * Adds an entry to the TAR file.
	 * 
	 * @param tarOutputStream the outputStream that will eventually be written out.
	 * @param targetFile the file to be added to the archive.
	 * @param entryName the name of the file within the archive.
	 * 
	 * @throws EBookAssemblyException if an IOException occurs during this process.
	 */
	final void addTarEntry(final TarOutputStream tarOutputStream, final File targetFile, final String entryName) throws EBookAssemblyException
	{
		TarEntry tarEntry = new TarEntry(targetFile);
        tarEntry.setName(entryName);

        try
        {
        	LOG.debug("Adding TAREntry: " + tarEntry.getName());
        	tarOutputStream.putNextEntry(tarEntry);
        }
        catch (IOException e)
        {
            String message = "An exception occurred while preparing file header to write.";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
	}

}
