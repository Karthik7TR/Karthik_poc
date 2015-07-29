/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.util.List;

/**
 * Implementors of the TocNode interface represent different kinds of nodes in a tree structure.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public interface TocNode {

	/**
	 * Retrieve the parent of this node.
	 * @return the parent.
	 */
	public TocNode getParent();
	
	/**
	 * Retrieve the children of this node.
	 * @return the children.
	 */
	public List<TocNode> getChildren();
	
	/**
	 * Retrieves the anchor reference for this node.
	 * @return the anchor reference.
	 */
	public String getAnchorReference();
	
	/**
	 * Retrieves the text for this node.
	 * @return the node text.
	 */
	public String getText();
	
	/**
	 * Adds a child to this node.
	 * @param child the child.
	 */
	public void addChild(TocNode child);
	
	/**
	 * Establishes a parent/child relation between this node and the specified parent.
	 * @param parent the parent.
	 */
	public void setParent(TocNode parent);
	
	public void setSplitTitle(String splitTitle);
	
	public String getSplitTitle();
	
	public String getTitleBreakString();

	public void setTitleBreakString(String titleBreakString);
	
	/**
	 * Assigns a toc node uuid to this toc node.
	 * @param tocNodeUuid the node uuid to assign.
	 */
	public void setTocNodeUuid(String tocNodeUuid);
	
	/**
	 * Assigns a document uuid to this node.
	 * @Param documentUuid the uuid to assign.
	 */
	public void setDocumentUuid(String documentUuid);

	/**
	 * Assigns text to this node.
	 * @param text the text to assign
	 */
	public void setText(String text);
	
	/**
	 * Retrieves the depth for this node.
	 * @return the depth, or level of nesting in XML terms.
	 */
	public int getDepth();
	
	/**
	 * Retrieves the document guid for this node, if it exists.
	 * 
	 * @return the document guid or an empty {@link String}.
	 */
	public String getDocumentGuid();
	
	/**
	 * Retrieves the toc guid for this node.
	 * @return the toc guid or an empty {@link String}.
	 */
	public String getTocGuid();
}
