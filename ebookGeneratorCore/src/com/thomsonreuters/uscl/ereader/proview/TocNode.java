package com.thomsonreuters.uscl.ereader.proview;

import java.util.List;

/**
 * Implementors of the TocNode interface represent different kinds of nodes in a tree structure.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public interface TocNode
{
    /**
     * Retrieve the parent of this node.
     * @return the parent.
     */
    TocNode getParent();

    /**
     * Retrieve the children of this node.
     * @return the children.
     */
    List<TocNode> getChildren();

    /**
     * Retrieves the anchor reference for this node.
     * @return the anchor reference.
     */
    String getAnchorReference();

    /**
     * Retrieves the text for this node.
     * @return the node text.
     */
    String getText();

    /**
     * Adds a child to this node.
     * @param child the child.
     */
    void addChild(TocNode child);

    /**
     * Establishes a parent/child relation between this node and the specified parent.
     * @param parent the parent.
     */
    void setParent(TocNode parent);

    void setSplitTitle(String splitTitle);

    String getSplitTitle();

    String getTitleBreakString();

    void setTitleBreakString(String titleBreakString);

    /**
     * Assigns a toc node uuid to this toc node.
     * @param tocNodeUuid the node uuid to assign.
     */
    void setTocNodeUuid(String tocNodeUuid);

    /**
     * Assigns a document uuid to this node.
     * @Param documentUuid the uuid to assign.
     */
    void setDocumentUuid(String documentUuid);

    /**
     * Assigns text to this node.
     * @param text the text to assign
     */
    void setText(String text);

    /**
     * Retrieves the depth for this node.
     * @return the depth, or level of nesting in XML terms.
     */
    int getDepth();

    /**
     * Retrieves the document guid for this node, if it exists.
     *
     * @return the document guid or an empty {@link String}.
     */
    String getDocumentGuid();

    /**
     * Retrieves the toc guid for this node.
     * @return the toc guid or an empty {@link String}.
     */
    String getTocGuid();
}
