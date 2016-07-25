/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class NovusLogCleaner {
	private static final Logger log = LogManager.getLogger(NovusLogCleaner.class);
	
	private static final String NOVUS_LOG_FOLDER = "/home/asadmin/";
	
	/*
	 * Clean up Novus generated Log files every 12 hours so home directory disk space does
	 * not fill up
	 */
	@Scheduled(fixedRate = 12*60*60*1000)
	public void cleanupOldNovusFiles() 
	{
		File dir = new File(NOVUS_LOG_FOLDER);
		
		if(dir.isDirectory())
		{
			FileFilter fileFilter = new FileFilter() 
			{
				public boolean accept(File dir) 
				{
					if(dir.isFile())
					{
						String name = dir.getName();
						if(StringUtils.isNotBlank(name))
						{
							return name.matches("^MC-(Client|Prod).txt.\\d{2}-\\d{2}-\\d{4}$");
						}
					}
					
					return false;
				}
			};
			
			File[] files = dir.listFiles(fileFilter);
			
			for(File file : files)
			{
				try {
					log.debug("Novus Log Clean-up: " + file.getCanonicalPath());
					file.delete();
				} catch (IOException e) {
					log.debug("Novus Log Clean-up failed. " + e.getMessage());
				}
			}
		}
	}
}
