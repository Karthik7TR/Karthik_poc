/*
* Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkInternalLinksFilter extends XMLFilterImpl {
	
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private HashMap<String, String> anchorDupTargets;
	private ArrayList<String> unlinkDocMetadataList;
	private Map<String, DocMetadata> docMetadataKeyedByProViewId;
	private DocMetadata unlinkDocMetadata;
	private int badLinkCntr = 0;
	private int goodAnchorCntr = 0;
	private String currentGuid;
	
	private Pattern pattern = Pattern.compile("^er:#([a-zA-Z0-9_]+)/[a-zA-Z0-9_]+$");
	
	public String getCurrentGuid() 
	{
		return currentGuid;
	}
	public void setCurrentGuid(String currentGuid) 
	{
		this.currentGuid = currentGuid;
	}

	public void setTargetAnchors(HashMap<String, HashSet<String>> targetAnchors )
	{
		this.targetAnchors = targetAnchors;
	}
	public HashMap<String, HashSet<String>> getTargetAnchors()
	{
		return targetAnchors;
	}
	
	public void setAnchorDupTargets(HashMap<String, String> anchorDupTargets) 
	{
		this.anchorDupTargets = anchorDupTargets;
	}
	public HashMap<String, String> getAnchorDupTargets() 
	{
		return anchorDupTargets;
	}
	public ArrayList<String> getUnlinkDocMetadataList() 
	{
		return unlinkDocMetadataList;
	}
	public void setUnlinkDocMetadataList(
			ArrayList<String> unlinkDocMetadataList) 
	{
		this.unlinkDocMetadataList = unlinkDocMetadataList;
	}
	
	public DocMetadata getUnlinkDocMetadata() 
	{
		return unlinkDocMetadata;
	}
	public Map<String, DocMetadata> getDocMetadataKeyedByProViewId() {
		return docMetadataKeyedByProViewId;
	}
	public void setDocMetadataKeyedByProViewId(
			Map<String, DocMetadata> docMetadataKeyedByProViewId) {
		this.docMetadataKeyedByProViewId = docMetadataKeyedByProViewId;
	}
	public void setUnlinkDocMetadata(DocMetadata unlinkDocMetadata) 
	{
		this.unlinkDocMetadata = unlinkDocMetadata;
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (qName.equalsIgnoreCase("a"))
		{
			if (atts != null && atts.getValue("href") != null && 
					atts.getValue("href").startsWith("er:#") && atts.getValue("href").contains("/"))
			{
				String guid = currentGuid;
				String guidList[] = atts.getValue("href").split("/");

				if (guidList.length > 1 )
				{
					guid = guidList[0].substring(4);
				}


				if (targetAnchors != null)
				{
					nameAnchors =  targetAnchors.get(guid);
				}
				String attsHrefValue = atts.getValue("href");

				if ( nameAnchors != null && nameAnchors.contains(attsHrefValue))
				{
					if	( anchorDupTargets != null && anchorDupTargets.containsKey(attsHrefValue))
					{
					// change href REPLACEWITH existing anchor

						for ( Entry<String, String> dupTarget : anchorDupTargets.entrySet() )
						{
							if (dupTarget.getKey().contains(attsHrefValue))
							{	
								attsHrefValue  = dupTarget.getValue();
								if (!dupTarget.getValue().contains("_"))
									break; // give priority to guid without _
							}
						}
						AttributesImpl newAtts = new AttributesImpl(atts);
										
						if (attsHrefValue != null && newAtts.getIndex("href") >= 0 && !attsHrefValue.equals(newAtts.getValue("href")))
						{
							int indexHrefId = newAtts.getIndex("href");
							newAtts.setAttribute(indexHrefId, "", "", "href", "CDATA", attsHrefValue);
						}
						super.startElement(uri, localName, qName, newAtts);
						goodAnchorCntr++;

					}							
					else
					{
						// remove anchor with no target.
						badLinkCntr++;
						
						//write out link information for email report
						if (unlinkDocMetadataList == null)
						{
							unlinkDocMetadataList = new ArrayList<String>();
						}
						
						StringBuffer sbDocMetadata = new StringBuffer();
						if (unlinkDocMetadata != null)
						{
							sbDocMetadata.append(unlinkDocMetadata.getDocUuid());
						}
						else
						{
							sbDocMetadata.append(currentGuid);
						}
						sbDocMetadata.append(",");
						if(unlinkDocMetadata.getDocFamilyUuid() != null )
						{
							sbDocMetadata.append(unlinkDocMetadata.getDocFamilyUuid());
						}
						sbDocMetadata.append(",");
						if(unlinkDocMetadata.getNormalizedFirstlineCite() != null )
						{
							sbDocMetadata.append(unlinkDocMetadata.getNormalizedFirstlineCite());
						}
						sbDocMetadata.append(",");
						if(unlinkDocMetadata.getSerialNumber() != null )
						{
							sbDocMetadata.append(unlinkDocMetadata.getSerialNumber());
						}
						sbDocMetadata.append(",");
						if(unlinkDocMetadata.getCollectionName() != null )
						{
							sbDocMetadata.append(unlinkDocMetadata.getCollectionName());
						}
						sbDocMetadata.append(",");
						String link = atts.getValue("href");
						sbDocMetadata.append(link);

						Matcher matcher = pattern.matcher(link);
						if(matcher.find()) {
							String proViewId = matcher.group(1);
							DocMetadata targetDocMetadata = docMetadataKeyedByProViewId.get(proViewId);
							if(targetDocMetadata != null) {
								sbDocMetadata.append(",");
								if(StringUtils.isNotBlank(targetDocMetadata.getDocUuid())) 
								{
									sbDocMetadata.append(targetDocMetadata.getDocUuid());
								}
								sbDocMetadata.append(",");
								if(StringUtils.isNotBlank(targetDocMetadata.getDocFamilyUuid())) 
								{
									sbDocMetadata.append(targetDocMetadata.getDocFamilyUuid());
								}
								sbDocMetadata.append(",");
								if(StringUtils.isNotBlank(targetDocMetadata.getNormalizedFirstlineCite())) 
								{
									sbDocMetadata.append(targetDocMetadata.getNormalizedFirstlineCite());
								}
								sbDocMetadata.append(",");
								if(targetDocMetadata.getSerialNumber() != null) 
								{
									sbDocMetadata.append(targetDocMetadata.getSerialNumber());
								}
							}
						}
						
						unlinkDocMetadataList.add(sbDocMetadata.toString());
					}
				}
				else
				{
					super.startElement(uri, localName, qName, atts);
					goodAnchorCntr++;
				}
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
				goodAnchorCntr++;
			}
		}
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
			super.characters(buf, offset, len);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equalsIgnoreCase("a"))
		{
			if (badLinkCntr > 0) // an anchor has been removed
			{
				if (goodAnchorCntr == 0)
				{
					badLinkCntr--;
				}
				if (goodAnchorCntr > 0)
				{
					super.endElement(uri, localName, qName);
					goodAnchorCntr--;
				}	
			}
			else 
			{
				super.endElement(uri, localName, qName);
				if (goodAnchorCntr > 0)
				{
					goodAnchorCntr--;
				}
			}
		}
		else
		{
			super.endElement(uri, localName, qName);
		}
	}
	
}
