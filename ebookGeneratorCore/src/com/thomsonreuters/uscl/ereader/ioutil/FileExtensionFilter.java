/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.File;
import java.io.FileFilter;

/**
 * Generic FileFilter that filters files based on a set list of file extensions.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class FileExtensionFilter implements FileFilter
{
	private String[] acceptedFileExtensions;
	
	/**
	 * Returns the specified list of acceptable file extensions.
	 * 
	 * @return list of acceptable file extensions.
	 */
	public String[] getAcceptedFileExtensions()
	{
		return acceptedFileExtensions;
	}
	
	/**
	 * Sets the list of file extensions that should be accepted by this generic file filter.
	 * 
	 * @param extensions list of acceptable extensions
	 */
	public void setAcceptedFileExtensions(String[] extensions)
	{
		acceptedFileExtensions = extensions;
	}
	
	@Override
	public boolean accept(File file) 
	{
		if (acceptedFileExtensions == null || acceptedFileExtensions.length == 0)
		{
			throw new RuntimeException("Extension list not specified in the generic FileFilter");
		}
		
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
