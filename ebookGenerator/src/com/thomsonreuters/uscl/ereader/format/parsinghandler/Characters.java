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
public class Characters extends ParserEvent {

	private char[] buf;
	private int offset;
	private int len;
	
	public Characters(char[] buf, int offset, int len)
	{
		super(ParserEvent.CHAR_EVENT);
		
		this.buf = buf;
		this.offset = offset;
		this.len = len;
	}
	
	public char[] getBuffer()
	{
		return buf;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public int getLength()
	{
		return len;
	}
}
