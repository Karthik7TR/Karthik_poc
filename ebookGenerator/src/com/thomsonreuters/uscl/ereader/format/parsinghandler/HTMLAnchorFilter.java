/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLAnchorFilter extends XMLFilterImpl {
	
	private boolean isImageLink = false;
	private boolean isEmptyAnchor = false;
	
	private ImageService imgService;
	
	private int imgEncountered = 0;
	
	private long jobInstanceId;
	
	public void setimgService(ImageService imgService)
	{
		this.imgService = imgService;
	}
	
	public void setjobInstanceId(long jobInstanceId)
	{
		this.jobInstanceId = jobInstanceId;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (!isImageLink && !isEmptyAnchor)
		{
			if (qName.equalsIgnoreCase("a"))
			{
				if (atts != null)
				{
					//build image tag for image anchors
					if (atts.getValue("type") != null && atts.getValue("type").equalsIgnoreCase("image/jpeg"))
					{
						isImageLink = true;
						imgEncountered++;
						qName = "img";
						String imgGuid = "";
						String href = atts.getValue("href");
						if (!StringUtils.isEmpty(href))
						{
							imgGuid = href.substring(href.indexOf("/Blob/") + 6, href.indexOf(".jpg?"));
							
							if (imgGuid.length() > 34)
							{
								imgGuid = imgGuid.replace("v1/", "");
							}
						}
			
						if (imgGuid.length() >= 32 && imgGuid.length() < 34)
						{
							AttributesImpl newAtts = new AttributesImpl();
							newAtts.addAttribute("", "", "alt", "CDATA", 
									"Image " + imgEncountered + " within document.");
							newAtts.addAttribute("", "", "src", "CDATA", "er:#" + imgGuid);
							ImageMetadataEntityKey key = new ImageMetadataEntityKey(jobInstanceId, imgGuid);
							ImageMetadataEntity imgMetadata = imgService.findImageMetadata(key);
							
							if (imgMetadata.getHeight() > 668 || imgMetadata.getWidth() > 648)
							{
								newAtts.addAttribute("", "", "class", "CDATA", "tr_image");
							}
							
							super.startElement(uri, localName, qName, newAtts);
						}
						else
						{
							throw new SAXException("Could not retrieve valid image guid from an image anchor");
						}
					}
					//remove empty anchor tags
					else if (atts.getValue("href") != null && atts.getValue("href").equalsIgnoreCase("#"))
					{
						isEmptyAnchor = true;
					}
					else
					{
						super.startElement(uri, localName, qName, atts);
					}
				}
				else
				{
					super.startElement(uri, localName, qName, atts);
				}
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
			}
		}
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if (!isImageLink && !isEmptyAnchor)
		{
			super.characters(buf, offset, len);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (!isImageLink && !isEmptyAnchor)
		{
			super.endElement(uri, localName, qName);
		}
		else
		{
			if (qName.equalsIgnoreCase("a"))
			{
				if (isImageLink)
				{
					qName = "img";
					isImageLink = false;
					super.endElement(uri, localName, qName);
				}
				else if (isEmptyAnchor)
				{
					isEmptyAnchor = false;
				}
			}
		}
	}
}
