/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.FormatConstants;
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
	
    private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private static final Logger LOG = Logger.getLogger(HTMLAnchorFilter.class);

	private String currentGuid;
	

	private int dupEncountered = 0;

	private String firstlineCite;
	
	private String docGuid;
	

	public String getDocGuid() {
		return docGuid;
	}
	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}
	
	public void setTargetAnchors(HashMap<String, HashSet<String>> targetAnchors )
	{
		this.targetAnchors = targetAnchors;
	}
	public HashMap<String, HashSet<String>> getTargetAnchors()
	{
		return targetAnchors;
	}

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

	public String getCurrentGuid() 
	{
		return currentGuid;
	}

	public void setCurrentGuid(String currentGuid) 
	{
		this.currentGuid = currentGuid;
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
					String mime = atts.getValue("type");
					//build image tag for image anchors
					if (mime != null && (mime.equalsIgnoreCase("image/jpeg") ||
							mime.equalsIgnoreCase("image/x-png") || mime.equalsIgnoreCase("image/gif") ||
							mime.equalsIgnoreCase("image/png")))
					{
						isImageLink = true;
						imgEncountered++;
						qName = "img";
						String imgGuid = "";
						String href = atts.getValue("href");
						if (!StringUtils.isEmpty(href))
						{
							imgGuid = href.substring(href.indexOf("/Blob/") + 6, href.indexOf(imageSpecialExtension(mime)));
							
							if (imgGuid.length() > 34)
							{
								imgGuid = imgGuid.replace("v1/", "");
							}
						}
			
						if (imgGuid.length() >= 30 && imgGuid.length() <= 36)
						{
							AttributesImpl newAtts = new AttributesImpl();
							
/*							newAtts.addAttribute("", "", "alt", "CDATA", 
									"Image " + imgEncountered + " within " + firstlineCite + " document.");*/
							newAtts.addAttribute("", "", "src", "CDATA", FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX + imgGuid);
							ImageMetadataEntityKey key = new ImageMetadataEntityKey(jobInstanceId, imgGuid, docGuid);
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
						href = href.replace(href.substring(0, href.indexOf("/Link/Document/Blob/") + 20), FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX);
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
						String attsIdValue = atts.getValue("id");
						String attsHrefValue = atts.getValue("href");
						// set href to er:#docFamilyGuid/namedAnchor
						if(attsHrefValue != null && attsHrefValue.startsWith("#"))
						{
//                              Change to this format: href=�er:#currentDocFamilyGuid/namedAnchor�
							attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX + currentGuid +"/" +attsHrefValue.substring(1) ;
							
							// Temp fix for sp_pubnumber references from URL builder
							if( attsHrefValue.contains("_sp_"))
							{
								int idxHrefSpstart = attsHrefValue.indexOf("_sp_");
								int idxHrefSpend = attsHrefValue.substring(idxHrefSpstart+4).indexOf("_") + idxHrefSpstart+4;
								String removeSp = attsHrefValue.substring(idxHrefSpstart, idxHrefSpend);
								attsHrefValue  = attsHrefValue.replace(removeSp, "");													
							}
							
//                              And then add to Target list.
							HashSet<String> anchorSet = targetAnchors.get(currentGuid);
							if (anchorSet == null)
							{
								anchorSet = new HashSet<String>();
							}
							anchorSet.add(attsHrefValue);
							targetAnchors.put(currentGuid, anchorSet );	
						}
						else if(attsHrefValue != null && attsHrefValue.startsWith(FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT) )
						{
							if (!attsHrefValue.contains("/"))
							{
								//TODO: throw error for this scenario
								LOG.error("Internal link was badly formed. " + attsHrefValue +
										" was changed to er:#"+ currentGuid +"/" +attsHrefValue.substring(4));

//                              Change to this format: href=�er:#currentDocFamilyGuid/namedAnchor�
								attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX + currentGuid +"/" +attsHrefValue.substring(4) ;
							}
							// Temp fix for sp_pubnumber references from URL builder
							if( attsHrefValue.contains("_sp_"))
							{
								int idxHrefSpStart = attsHrefValue.indexOf("_sp_");
								int idxHrefSpEnd = attsHrefValue.substring(idxHrefSpStart+4).indexOf("_") + idxHrefSpStart+4;
								String removeSp = attsHrefValue.substring(idxHrefSpStart, idxHrefSpEnd);
								attsHrefValue  = attsHrefValue.replace(removeSp, "");													
							}
							
							//                          Add to Target list.	
							int indexOfSlash = StringUtils.indexOf(attsHrefValue, "/", attsHrefValue.indexOf("#"));				
							
							String guidLink = StringUtils.substring(attsHrefValue, attsHrefValue.indexOf("#") + 1, indexOfSlash)  ;
							HashSet<String> anchorSet = targetAnchors.get(guidLink);
							if (anchorSet == null)
							{
								anchorSet = new HashSet<String>();
								
							}
							String removeSplitTitle = "";
							if (attsHrefValue.startsWith(FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX)){
								removeSplitTitle = attsHrefValue;
							}
							else{
								removeSplitTitle = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX+StringUtils.substring(attsHrefValue, attsHrefValue.indexOf("#") + 1);
							}
							anchorSet.add(removeSplitTitle);
							targetAnchors.put(guidLink, anchorSet );
						}
						
						// Dedupe id Anchor Names
						if( nameAnchors != null && attsIdValue != null && nameAnchors.contains(atts.getValue("id")))
						{
							dupEncountered++;
							String idAnchor = attsIdValue + "dup" + dupEncountered ;
							
							AttributesImpl newAtts = new AttributesImpl(atts);
							
							int indexId = newAtts.getIndex("id");
							if (indexId > -1)
							{
								newAtts.setAttribute(indexId, "", "", "id", "CDATA", idAnchor);
							}
							if (attsHrefValue != null && newAtts.getIndex("href") >= 0 && !attsHrefValue.equals(newAtts.getValue("href")))
							{
								int indexHrefId = newAtts.getIndex("href");
								newAtts.setAttribute(indexHrefId, "", "", "href", "CDATA", attsHrefValue);
							}
							super.startElement(uri, localName, qName, newAtts);
						}
						else
						{
							if(nameAnchors == null)
							{
								nameAnchors = new HashSet<String>();
							}
							if (attsIdValue != null)
							{
								nameAnchors.add(attsIdValue);
							}	
							AttributesImpl newAtts = new AttributesImpl(atts);
							
							if (attsHrefValue != null && newAtts.getIndex("href") >= 0 && !attsHrefValue.equals(newAtts.getValue("href")))
							{
								int indexHrefId = newAtts.getIndex("href");
								newAtts.setAttribute(indexHrefId, "", "", "href", "CDATA", attsHrefValue);
							}
							super.startElement(uri, localName, qName, newAtts);
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
	
	private String imageSpecialExtension(String imageMime) {
		String extension = "";
		
		if (imageMime.equalsIgnoreCase("image/jpeg")) {
			extension = ".jpg?";
		} else if (imageMime.equalsIgnoreCase("image/gif")) {
			extension = ".gif?";
		} else {
			extension = ".png?";
		}
		
		return extension;
	}
}
