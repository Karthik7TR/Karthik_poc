/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Instances of PlaceholderDocumentFilter parse a template xml document and produce xml content.  In practice this class is used to
 * inject labels from the TOC into a placeholder document (hence the name).
 * 
 * <p>In order for this filter to work as intended, the XML document template must have an empty element as follows: "&lt;displaytext/&gt;".
 * The text passed in to this filter's constructor will replace a &lt;displaytext/&gt; element.  All &lt;displaytext&gt; tags will be replaced.</p>
 * <p><em>Parsing a document that does not contain a &lt;displaytext/&gt; element results in the identity transform.</em></p>
 * <p><strong>Usage:</strong></p>
 * <p>Given this XML document - &lt;html&gt;&lt;head/&gt;&lt;body&gt;&lt;div&gt;&lt;displaytext/&gt;&lt;/div&gt;&lt;/body&gt;&lt;/html&gt;</p>
 * <p>And a PlaceholderDocumentFilter constructed with the displayText argument set to the {@link String} "YARR!"</p>
 * <p>The resultant document, when parsed by the PlaceholderDocumentFilter, would be - &lt;html&gt;&lt;head/&gt;&lt;body&gt;&lt;div&gt;YARR!&lt;/div&gt;&lt;/body&gt;&lt;/html&gt;</p>
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class PlaceholderDocumentFilter extends XMLFilterImpl {

	private static final String DISPLAY_TEXT = "displaytext";
	private String displayText;
	
	/**
	 * Single argument constructor that takes the display text to be rendered within the document's body.
	 * 
	 * @param displayText the text to be displayed within the body of the document.
	 */
	public PlaceholderDocumentFilter(String displayText) {
		this.displayText = displayText;
	}

	@Override
	public void startElement(String uri, String localname, String qName,
			Attributes attributes) throws SAXException {
		if (DISPLAY_TEXT.equals(qName)) {
			super.characters(displayText.toCharArray(), 0, displayText.length());
		}
		else {
			super.startElement(uri, localname, qName, attributes);
		}
	}
	
	@Override
	public void endElement(String uri, String localname, String qName) throws SAXException {
		if (DISPLAY_TEXT.equals(qName)) {
			//eat this event.
		}
		else {
			super.endElement(uri, localname, qName);
		}
	}
	
}
