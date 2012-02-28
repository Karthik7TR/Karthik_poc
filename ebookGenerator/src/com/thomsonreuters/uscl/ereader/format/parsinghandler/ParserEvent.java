/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

/**
 * Base class that defines the order the event was fired off and the type of event.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class ParserEvent {

	public static final int START_EVENT = 0;
	public static final int CHAR_EVENT = 1;
	public static final int END_EVENT = 2;
	
	private int eventType;
	
	public ParserEvent(int eventType)
	{
		this.eventType = eventType;
	}
	
	public int getEventType()
	{
		return this.eventType;
	}
}
