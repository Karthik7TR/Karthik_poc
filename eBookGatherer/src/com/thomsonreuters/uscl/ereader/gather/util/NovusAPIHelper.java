/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.westgroup.novus.productapi.Novus;
/**
 * Base class to reusable code for all Novus Api interaction.
 * can be used by TOC,NORT and DOC data retrieval.  
 * 
 * @author U0105927
 *
 */
public class NovusAPIHelper 
{
	// TODO: method to return connection object 
	// TODO: convert this hardcodding to read data from properties file.
	private static final String novusEnvironment = EBConstants.NOVUSE_NVIRONMENT; //System.getProperty((String) "novusEnvironment"); //"Client" "Prod";  
	
	/**
	 * Connecting to Novus system we can specify which environment connect using 
	 * class variable 'novusEnvironment'   
	 * @return
	 */
	public Novus getNovusObject()
	{
		Novus novus = new Novus();

		novus.setQueueCriteria(null, novusEnvironment);
		novus.setResponseTimeout(30000);
		novus.useLatestPit();
		// need to identify E-readerBuilder with Novus api for logging
		// purpose.
		novus.setProductName("EBOOKGENERATOR-USCL");// EReaderBuilder
		novus.setBusinessUnit("WestCobalt");
		
		return novus;
	}
	
	/**
	 * creating this separate method so that in future if we need to do some 
	 * cleanup work while closing connection.As we learn more about Novus api. 
	 * @param novus
	 */
	public void closeNovusConnection(Novus novus)
	{

    	novus.shutdownMQ();
		
	}
	
	
	/**
	 * Some of the TOCs are versioned so we need to set timestamp value before
	 * accessing any data further.
	 * 
	 * @return
	 */
	public String getCurrentDateTime() {
		final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String dateTime = sdf.format(cal.getTime());
		return dateTime;
	}

	/**
	 * remove heading and trailing tags from TOC names.
	 * @param nameValue
	 * @return
	 */
	public String processName(String nameValue)
	{
		CharSequence startElement = EBConstants.HEADING_START_TAG;
		CharSequence endElement = EBConstants.HEADING_END_TAG;
		CharSequence replacement = "";
		nameValue = nameValue.replace(startElement, replacement);
		nameValue = nameValue.replace(endElement , replacement);
		return nameValue;
	}

	// TODO some method to parse response and converting them in to usable domain object. 
	
	// TODO Some file operation to store toc and document data.
	
	
	
}
