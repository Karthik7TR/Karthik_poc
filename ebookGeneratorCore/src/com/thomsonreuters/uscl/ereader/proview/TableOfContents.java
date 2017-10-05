package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a table of contents structure. TableOfContents is a container node that has no parent.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TableOfContents implements TocNode {
    private static final String EMPTY_STRING = "";
    private List<TocNode> children = new ArrayList<>();

    public TableOfContents() {
    }

    @Override
    public void addChild(final TocNode child) {
        children.add(child);
    }

    public void setChildren(final List<TocNode> children) {
        this.children = children;
    }

    @Override
    public List<TocNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public void setSplitTitle(final String splitTitle) {
        //Intentionally left blank
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
    public void setTitleBreakString(final String titleBreakString) {
        //Intentionally left blank
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
    public void setParent(final TocNode parent) {
        //no op, TableOfContents has no parent.
    }

    @Override
    public void setTocNodeUuid(final String tocNodeUuid) {
        //no op, TableOfContents has no uuid.
    }

    @Override
    public void setDocumentUuid(final String documentUuid) {
        //no op, TableOfContents has no uuid.
    }

    @Override
    public void setText(final String text) {
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
