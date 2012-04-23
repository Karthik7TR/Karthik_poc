/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
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
	private static final String ANCHOR_CASCADE_TAG = "anchorsToCascade";
	private static final String ANCHOR_TAG = "a";
	private String displayText;
	private String tocGuid;
	private List<String> anchors;
	
	/**
	 * Single argument constructor that takes the display text to be rendered within the document's body.
	 * 
	 * @param displayText the text to be displayed within the body of the document.
	 */
	public PlaceholderDocumentFilter(String displayText, String tocGuid, List<String> anchors) {
		this.displayText = displayText;
		this.tocGuid = tocGuid;
		this.anchors = anchors;
	}

	@Override
	public void startElement(String uri, String localname, String qName,
			Attributes attributes) throws SAXException {
		if (DISPLAY_TEXT.equals(qName)) {
			super.characters(displayText.toCharArray(), 0, displayText.length());
		}
		else if (ANCHOR_CASCADE_TAG.equals(qName))
		{
			for (String anchor : anchors)
			{
				AttributesImpl newAtts = new AttributesImpl();
				newAtts.addAttribute("", "", "name", "CDATA", anchor);
				super.startElement("", ANCHOR_TAG, ANCHOR_TAG, newAtts);
				super.endElement("", ANCHOR_TAG, ANCHOR_TAG);
			}
		}
		else if (ANCHOR_TAG.equals(qName)) {
			String anchorName = attributes.getValue("name");
			if (anchorName != null && anchorName.equalsIgnoreCase("placeholder_anchor"))
			{
				AttributesImpl newAtts = new AttributesImpl(attributes);
				newAtts.removeAttribute(newAtts.getIndex("name"));
				newAtts.addAttribute("", "", "name", "CDATA", tocGuid);

				super.startElement(uri, localname, qName, newAtts);
			}
			else
			{
				super.startElement(uri, localname, qName, attributes);
			}
		}
		else {
			super.startElement(uri, localname, qName, attributes);
		}
	}
	
	@Override
	public void endElement(String uri, String localname, String qName) throws SAXException {
		if (DISPLAY_TEXT.equals(qName) || ANCHOR_CASCADE_TAG.equals(qName)) {
			//eat this event.
		}
		else {
			super.endElement(uri, localname, qName);
		}
	}
	
}
