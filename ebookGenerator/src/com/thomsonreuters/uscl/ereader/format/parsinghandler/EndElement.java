/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

/**
 * This object represent the buffered up character of an XML tag and is used to save off buffered up tags.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class EndElement extends ParserEvent {
	private String uri;
	private String localName;
	private String qName;
	
	public EndElement(String uri, String localName, String qName)
	{
		super(ParserEvent.END_EVENT);
		
		this.uri = uri;
		this.localName = localName;
		this.qName = qName;
	}
	
	public String getUri()
	{
		return uri;
	}
	
	public String getLocalName()
	{
		return localName;
	}
	
	public String getQName()
	{
		return qName;
	}
}
