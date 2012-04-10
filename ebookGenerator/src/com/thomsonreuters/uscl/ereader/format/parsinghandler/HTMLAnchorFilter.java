/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;

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
	private boolean isPDFLink = false;
	private boolean isEmptyAnchor = false;
	
	private ImageService imgService;
	
	private long imgMaxHeight = 668L;
	private long imgMaxWidth = 648L;
	
	private int imgEncountered = 0;
	
	private long jobInstanceId;
	private ArrayList<String> nameAnchors;
	
	private String firstlineCite;
	
	public void setimgService(ImageService imgService)
	{
		this.imgService = imgService;
	}
	
	public void setjobInstanceId(long jobInstanceId)
	{
		this.jobInstanceId = jobInstanceId;
	}
	
	public void setFirstlineCite(String firstlineCite)
	{
		this.firstlineCite = firstlineCite;
	}
	
	public void setImgMaxHeight(long maxHeight)
	{
		this.imgMaxHeight = maxHeight;
	}
	
	public long getImgMaxHeight()
	{
		return imgMaxHeight;
	}
	
	public void setImgMaxWidth(long maxWidth)
	{
		this.imgMaxWidth = maxWidth;
	}
	
	public long getImgMaxWidth()
	{
		return imgMaxWidth;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (!isImageLink && !isEmptyAnchor && !isPDFLink)
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
									"Image " + imgEncountered + " within " + firstlineCite + " document.");
							newAtts.addAttribute("", "", "src", "CDATA", "er:#" + imgGuid);
							ImageMetadataEntityKey key = new ImageMetadataEntityKey(jobInstanceId, imgGuid);
							ImageMetadataEntity imgMetadata = imgService.findImageMetadata(key);
							
							if (imgMetadata.getHeight() > imgMaxHeight 
									|| imgMetadata.getWidth() > imgMaxWidth)
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
					else if (atts.getValue("type") != null && atts.getValue("type").equalsIgnoreCase("application/pdf"))
					{
						isPDFLink = true;
						String href = atts.getValue("href");
						href = href.replace(href.substring(0, href.indexOf("/Link/Document/Blob/") + 20), "er:#");
						href = href.substring(0, href.indexOf(".pdf"));
						
						AttributesImpl newAtts = new AttributesImpl();
						
						newAtts.addAttribute("", "", "href", "CDATA", href);
						if (atts.getValue("class") != null)
						{
							newAtts.addAttribute("", "", "class", "CDATA", atts.getValue("class"));
						}
						
						super.startElement(uri, localName, qName, newAtts);
					}
					//remove empty anchor tags
					else if (atts.getValue("href") != null && atts.getValue("href").equalsIgnoreCase("#"))
					{
						isEmptyAnchor = true;
					}
					else
					{	
						// Dedupe id Anchor Names
						if( nameAnchors != null && atts.getValue("id") != null && nameAnchors.contains(atts.getValue("id")))
						{
							String idAnchor = atts.getValue("id") + "dup" + nameAnchors.size() ;
							
							AttributesImpl newAtts = new AttributesImpl(atts);
							
							int indexId = newAtts.getIndex("id");
							if (indexId > -1)
							{
								newAtts.setAttribute(indexId, "", "", "id", "CDATA", idAnchor);
							}
							super.startElement(uri, localName, qName, newAtts);
						}
						else
						{
							if(nameAnchors == null)
							{
								nameAnchors = new ArrayList<String>();
							}
							if (atts.getValue("id")!= null)
							{
								nameAnchors.add(atts.getValue("id"));
							}	
							super.startElement(uri, localName, qName, atts);
						}						
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
		if (!isImageLink && !isEmptyAnchor && !isPDFLink)
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
				else if (isPDFLink)
				{
					isPDFLink = false;
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
