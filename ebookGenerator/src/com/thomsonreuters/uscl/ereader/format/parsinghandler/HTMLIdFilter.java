/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.service.HTMLTransformerServiceImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLIdFilter extends XMLFilterImpl {
	
	private String currentGuid;
	private String familyGuid;
	private HashSet<String> nameAnchors;
	private HashMap<String, HashSet<String>> targetAnchors;
	private HashMap<String, HashSet<String>> dupTargetAnchors;
	private Map<String, String> dupGuids;
	private int anchorAddedCntr = 0;
	private int goodStartCntr = 0;
	private ArrayList<Integer> goodCntrList = new ArrayList<Integer>();
	
	private static final Logger LOG = Logger.getLogger(HTMLIdFilter.class);

	public String getCurrentGuid() 
	{
		return currentGuid;
	}
	public void setCurrentGuid(String currentGuid) 
	{
		this.currentGuid = currentGuid;
	}
	public String getFamilyGuid() 
	{
		return familyGuid;
	}
	public void setFamilyGuid(String familyGuid) 
	{
		this.familyGuid = familyGuid;
	}
	public void setTargetAnchors(HashMap<String, HashSet<String>> targetAnchors )
	{
		this.targetAnchors = targetAnchors;
	}
	public HashMap<String, HashSet<String>> getTargetAnchors()
	{
		return targetAnchors;
	}
	public void setDupTargetAnchors(HashMap<String, HashSet<String>> dupTargetAnchors )
	{
		this.dupTargetAnchors = dupTargetAnchors;
	}
	public HashMap<String, HashSet<String>> getDupTargetAnchors()
	{
		return dupTargetAnchors;
	}
	public void setDupGuids(Map<String, String> dupGuids) 
	{
		this.dupGuids = dupGuids;
	}
	public Map<String, String> getDupGuids() 
	{
		return dupGuids;
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		boolean bCreateDupAnchor = false;
		String guid = currentGuid; // proviewId or guid
				if (atts != null && atts.getValue("id") != null)
				{
					String guidList[] = atts.getValue("id").split("/");
					if (guidList.length > 1)
					{
						guid = guidList[0];
					}

					if(targetAnchors != null)
					{
					nameAnchors =  targetAnchors.get(guid);
					}
					// add anchor id after <span id="co_footnote_I6a32d800568d11e199970000837bc6dd"> like
					// <a name="er:#I38680d13677511dc8ebc0000837214a9/co_footnote_I6a32d800568d11e199970000837bc6dd">
					String fullyQualifiedId = "er:#" + guid + "/" + atts.getValue("id");
					
					if (fullyQualifiedId.contains("I0D0DBF80A33011E4AF1FB9529643FEAA")){
						LOG.info(" fullyQualifiedId "+fullyQualifiedId);
					}
					
					//determine if should be duplicate guid instead!
					if ( ((nameAnchors != null && !nameAnchors.contains(fullyQualifiedId)) || nameAnchors == null)
							&& dupGuids != null && dupGuids.containsKey(guid))
					{
						for (String dupProviewId : dupGuids.keySet())
						{
							String famGuid = dupGuids.get(dupProviewId);
							if ( famGuid.equals(familyGuid) && !guid.equals(dupProviewId))
							{
								String fullyQualifiedDupId = "er:#" + dupProviewId + "/" + atts.getValue("id");
								HashSet<String> nameDupAnchors =  targetAnchors.get(dupProviewId);
	
								if (nameDupAnchors != null && nameDupAnchors.contains(fullyQualifiedDupId))
								{
									
									bCreateDupAnchor=true;
									
									// Create a file with guid, fullyQualified and fullyQualifiedDupId
									HashSet<String> hs = new HashSet<String>();
									if (dupTargetAnchors != null && dupTargetAnchors.get(guid) != null)
									{
										hs = dupTargetAnchors.get(guid);
									}
									else if (dupTargetAnchors == null)
									{
										dupTargetAnchors = new HashMap<String,HashSet<String>>();
									}
									hs.add(fullyQualifiedDupId + "REPLACEWITH" + fullyQualifiedId);
									dupTargetAnchors.put(guid, hs);
								}
							}
						}
						if (bCreateDupAnchor==true)
						 {
							// Found a match!
							// Add anchor with fullyQualified but don't remove from list so we can change
							// the anchor href in the unlink step
							AttributesImpl newAtts = new AttributesImpl();
							
							newAtts.addAttribute( "", "", "name", "CDATA", atts.getValue("id"));
							
							if (anchorAddedCntr > 0)
							{
								goodCntrList.add(goodStartCntr);
								goodStartCntr = 0;
							}
							anchorAddedCntr++;
							super.startElement(uri, localName, qName, atts);
							super.startElement(uri, localName, "a", newAtts);
						 }
					}
					if (nameAnchors != null && nameAnchors.contains(fullyQualifiedId))
					{
						// insert missing named anchor
						AttributesImpl newAtts = new AttributesImpl();
							
							newAtts.addAttribute( "", "", "name", "CDATA", atts.getValue("id"));
							
							if (anchorAddedCntr > 0)
							{
								goodCntrList.add(goodStartCntr);
								goodStartCntr = 0;
							}
							anchorAddedCntr++;
							// remove from list
							nameAnchors.remove(fullyQualifiedId);
							targetAnchors.get(guid).remove(fullyQualifiedId);
							// write existing id
							super.startElement(uri, localName, qName, atts);
							super.startElement(uri, localName, "a", newAtts);
					}
					else if (bCreateDupAnchor==false)
					{
						super.startElement(uri, localName, qName, atts);
						if (anchorAddedCntr > 0)
						{
							goodStartCntr++;
						}
					}						
			}
			else
			{
				super.startElement(uri, localName, qName, atts);
				if (anchorAddedCntr > 0)
				{
					goodStartCntr++;
				}
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
		
		if (anchorAddedCntr> 0)
		{
			if (goodStartCntr == 0)
			{
				super.endElement(uri, localName, "a");
				anchorAddedCntr--;
				if (anchorAddedCntr >= 1)
				{
					int lastIdx = goodCntrList.size()-1;
					goodStartCntr = goodCntrList.get(lastIdx);
					goodCntrList.remove(lastIdx);
				}
			}
			else
			{
				goodStartCntr--;			
			}
		}
		super.endElement(uri, localName, qName);
	}
}
