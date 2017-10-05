package com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class RelationshipNode implements Comparable<RelationshipNode> {
    private String nortGuid;
    private String parentNortGuid;
    private RelationshipNode parentNode;
    private List<RelationshipNode> childNodes = new ArrayList<>();
    private Integer nortRank;
    private double rank;
    private String label;
    private String startDateStr;
    private String endDateStr;
    private String documentGuid;
    private String nodeType;
    private List<String> views = new ArrayList<>();
    // Default to false.  Will be set to true if element is found
    private boolean isRootNode;
    private boolean pubTaggedHeadingExists;

    public String getNortGuid() {
        return nortGuid;
    }

    public void setNortGuid(final String nortGuid) {
        this.nortGuid = nortGuid;
    }

    public String getParentNortGuid() {
        return parentNortGuid;
    }

    public void setParentNortGuid(final String parentNortGuid) {
        this.parentNortGuid = parentNortGuid;
    }

    public RelationshipNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(final RelationshipNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<RelationshipNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(final List<RelationshipNode> children) {
        childNodes = children;
    }

    public Integer getNortRank() {
        return nortRank;
    }

    public void setNortRank(final Integer nortRank) {
        this.nortRank = nortRank;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(final double rank) {
        this.rank = rank;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(final String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(final String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public boolean isRootNode() {
        return isRootNode;
    }

    public void setRootNode(final boolean rootNode) {
        isRootNode = rootNode;
    }

    public String getDocumentGuid() {
        return documentGuid;
    }

    public void setDocumentGuid(final String documentGuid) {
        this.documentGuid = documentGuid;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(final String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean getPubTaggedHeadingExists() {
        return pubTaggedHeadingExists;
    }

    public void setPubTaggedHeadingExists(final boolean pubTaggedHeadingExists) {
        this.pubTaggedHeadingExists = pubTaggedHeadingExists;
    }

    public List<String> getViews() {
        return views;
    }

    public void setViews(final List<String> views) {
        this.views = views;
    }

    public String getTocHierarchy() {
        final ArrayDeque<String> stack = new ArrayDeque<>();
        getParentLabels(parentNode, stack);

        final StringBuffer buffer = new StringBuffer();
        while (!stack.isEmpty()) {
            buffer.append(stack.pop());
            buffer.append("|");
        }
        return buffer.toString();
    }

    private void getParentLabels(final RelationshipNode parent, final ArrayDeque<String> stack) {
        if (parent != null) {
            stack.push(parent.getLabel());
            getParentLabels(parent.getParentNode(), stack);
        }
    }

    public boolean isDeletedNode() {
        int deletedViewCount = 0;
        for (final String view : views) {
            if (view.matches("(^DELER_[a-zA-z0-9_\\-]+)|([a-zA-z0-9_\\-]+DEL$)")) {
                deletedViewCount++;
            }
        }

        final int viewCount = views.size();
        return viewCount != 0 && deletedViewCount == viewCount;
    }

    @Override
    public int compareTo(final RelationshipNode o) {
        // Order by NORT rank first
        final Integer nodeOneNortRank = getNortRank();
        final Integer nodeTwoNortRank = o.getNortRank();

        // If NORT rank is tied, rank by TOC rank.
        final Double nodeOneRank = getRank();
        final Double nodeTwoRank = o.getRank();

        if (nodeOneNortRank > nodeTwoNortRank) {
            return 1;
        } else if (nodeOneNortRank < nodeTwoNortRank) {
            return -1;
        } else {
            if (nodeOneRank > nodeTwoRank) {
                return 1;
            } else if (nodeOneRank < nodeTwoRank) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((childNodes == null) ? 0 : childNodes.hashCode());
        result = prime * result + ((documentGuid == null) ? 0 : documentGuid.hashCode());
        result = prime * result + ((endDateStr == null) ? 0 : endDateStr.hashCode());
        result = prime * result + (isRootNode ? 1231 : 1237);
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((nortGuid == null) ? 0 : nortGuid.hashCode());
        result = prime * result + ((parentNode == null) ? 0 : parentNode.hashCode());
        result = prime * result + ((parentNortGuid == null) ? 0 : parentNortGuid.hashCode());
        final long temp;
        temp = Double.doubleToLongBits(rank);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((startDateStr == null) ? 0 : startDateStr.hashCode());
        result = prime * result + ((views == null) ? 0 : views.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RelationshipNode other = (RelationshipNode) obj;
        if (childNodes == null) {
            if (other.childNodes != null)
                return false;
        } else if (!childNodes.equals(other.childNodes))
            return false;
        if (documentGuid == null) {
            if (other.documentGuid != null)
                return false;
        } else if (!documentGuid.equals(other.documentGuid))
            return false;
        if (endDateStr == null) {
            if (other.endDateStr != null)
                return false;
        } else if (!endDateStr.equals(other.endDateStr))
            return false;
        if (isRootNode != other.isRootNode)
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (nodeType == null) {
            if (other.nodeType != null)
                return false;
        } else if (!nodeType.equals(other.nodeType))
            return false;
        if (nortGuid == null) {
            if (other.nortGuid != null)
                return false;
        } else if (!nortGuid.equals(other.nortGuid))
            return false;
        if (parentNode == null) {
            if (other.parentNode != null)
                return false;
        } else if (!parentNode.equals(other.parentNode))
            return false;
        if (parentNortGuid == null) {
            if (other.parentNortGuid != null)
                return false;
        } else if (!parentNortGuid.equals(other.parentNortGuid))
            return false;
        if (Double.doubleToLongBits(rank) != Double.doubleToLongBits(other.rank))
            return false;
        if (startDateStr == null) {
            if (other.startDateStr != null)
                return false;
        } else if (!startDateStr.equals(other.startDateStr))
            return false;
        if (views == null) {
            if (other.views != null)
                return false;
        } else if (!views.equals(other.views))
            return false;
        return true;
    }
}
