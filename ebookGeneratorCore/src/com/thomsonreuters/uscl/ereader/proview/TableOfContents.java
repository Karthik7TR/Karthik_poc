/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a table of contents structure. TableOfContents is a container node that has no parent.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TableOfContents implements TocNode {
	private static final String EMPTY_STRING = "";
	private List<TocNode> children = new ArrayList<TocNode>();
	
	public TableOfContents(){}
	
	public void addChild(TocNode child) {
		children.add(child);
	}
	
	public void setChildren(List<TocNode> children) {
		this.children = children;
	}
	
	public List<TocNode> getChildren(){
		return children;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public void setSplitTitle(String splitTitle) {
	}
	
	@Override
	public String getSplitTitle() {
		return EMPTY_STRING;
	}
	
	@Override
	public String getTitleBreakString() {
		return EMPTY_STRING;
	}

	@Override
	public void setTitleBreakString(String titleBreakString) {
	}

	@Override
	public TocNode getParent() {
		return null;
	}

	@Override
	public String getAnchorReference() {
		return EMPTY_STRING;
	}

	@Override
	public String getText() {
		return EMPTY_STRING;
	}

	@Override
	public void setParent(TocNode parent) {
		//no op, TableOfContents has no parent.
	}

	@Override
	public void setTocNodeUuid(String tocNodeUuid) {
		//no op, TableOfContents has no uuid.
	}

	@Override
	public void setDocumentUuid(String documentUuid) {
		//no op, TableOfContents has no uuid.
	}

	@Override
	public void setText(String text) {
		//no op, TableOfContents has no text.
		
	}

	@Override
	public int getDepth() {
		//depth of the table of contents is zero.
		return 0;
	}

	@Override
	public String getDocumentGuid() {
		return EMPTY_STRING;
	}

	@Override
	public String getTocGuid() {
		return EMPTY_STRING;
	}
}
