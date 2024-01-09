package com.thomsonreuters.uscl.ereader.proview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class represents a single entry in the TOC manifest within title.xml.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TocEntry implements TocNode, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SOLIDUS = "/";
    protected String tocGuid;
    protected String docGuid;
    protected String text;
    protected List<TocNode> children = new ArrayList<>();
    protected TocNode parent;
    private int depth;
    protected String splitTitle;
    protected String titleBreakString;

    public TocEntry(final int depth) {
        this.depth = depth;
    }

    public TocEntry(final String tocGuid, final String docGuid, final String text, final int depth) {
        this.tocGuid = tocGuid;
        this.docGuid = docGuid;
        this.text = text;
        this.depth = depth;
    }

    @Override
    public String getAnchorReference() {
        if (!StringUtils.isBlank(splitTitle)) {
            return (docGuid != null) ? splitTitle + "#" + docGuid + SOLIDUS + tocGuid : splitTitle + "#" + tocGuid;
        }
        return (docGuid != null) ? docGuid + SOLIDUS + tocGuid : tocGuid;
    }

    @Override
    public String getSplitTitle() {
        return splitTitle;
    }

    @Override
    public void setSplitTitle(final String splitTitle) {
        this.splitTitle = splitTitle;
    }

    @Override
    public String getTitleBreakString() {
        return titleBreakString;
    }

    @Override
    public void setTitleBreakString(final String titleBreakString) {
        this.titleBreakString = titleBreakString;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setChildren(final List<TocNode> children) {
        this.children = children;
    }

    @Override
    public List<TocNode> getChildren() {
        return children;
    }

    @Override
    public void setParent(final TocNode parent) {
        this.parent = parent;
    }

    @Override
    public TocNode getParent() {
        return parent;
    }

    @Override
    public void addChild(final TocNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public void setTocNodeUuid(final String tocNodeUuid) {
        tocGuid = tocNodeUuid;
    }

    @Override
    public void setDocumentUuid(final String documentUuid) {
        docGuid = documentUuid;
    }

    @Override
    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public String getDocumentGuid() {
        return docGuid;
    }

    @Override
    public String getTocGuid() {
        return tocGuid;
    }
}
