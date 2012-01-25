/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Wipes out any processing instructions that may be in the XML document that is parsed by a parser that uses this filter.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProcessingInstructionZapperFilter extends XMLFilterImpl {

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		//Do not process PIs.
	}
}
