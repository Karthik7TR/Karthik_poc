/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents a single entry in the TOC manifest within title.xml.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TocEntry implements TocNode {
	private static final String SOLIDUS = "/";
	protected String tocGuid;
	protected String docGuid;
	protected String text;
	protected List<TocNode> children = new ArrayList<TocNode>();
	protected TocNode parent;
	private int depth;

	public TocEntry(int depth){
		this.depth = depth;
	}
	
	public TocEntry (String tocGuid, String docGuid, String text, int depth) {
		this.tocGuid = tocGuid; 
		this.docGuid = docGuid;
		this.text = text;
		this.depth = depth;
	}
		
	public String getAnchorReference() {
		return (docGuid != null) ? docGuid + SOLIDUS + tocGuid : tocGuid;
	}

	public String getText() {
		return text;
	}
	
	public void setChildren(List<TocNode> children){
		this.children = children;
	}
	
	@Override
	public List<TocNode> getChildren() {
		return children;
	}
	
	@Override
	public void setParent(TocNode parent) {
		this.parent = parent;
	}
	
	@Override
	public TocNode getParent() {
		return parent;
	}

	@Override
	public void addChild(TocNode child) {
		this.children.add(child);
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public void setTocNodeUuid(String tocNodeUuid) {
		this.tocGuid = tocNodeUuid;
		
	}

	@Override
	public void setDocumentUuid(String documentUuid) {
		this.docGuid = documentUuid;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int getDepth() {
		return this.depth;
	}

	@Override
	public String getDocumentGuid() {
		return this.docGuid;
	}

	@Override
	public String getTocGuid() {
		return this.tocGuid;
	}
}
