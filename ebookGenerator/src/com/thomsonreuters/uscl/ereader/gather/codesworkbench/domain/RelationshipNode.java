/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RelationshipNode implements Comparable<RelationshipNode> {
	private String nortGuid;
	private String parentNortGuid;
	private RelationshipNode parentNode;
	private List<RelationshipNode> childNodes = new ArrayList<RelationshipNode>();
	private double rank;
	private String label;
	private String startDateStr;
	private String endDateStr;
	private String documentGuid;
	private String nodeType;
	private List<String> views = new ArrayList<String>();
	// Default to false.  Will be set to true if element is found
	private boolean isRootNode = false;
	private boolean pubTaggedHeadingExists = false;
	
	
	public String getNortGuid() {
		return nortGuid;
	}
	public void setNortGuid(String nortGuid) {
		this.nortGuid = nortGuid;
	}
	public String getParentNortGuid() {
		return parentNortGuid;
	}
	public void setParentNortGuid(String parentNortGuid) {
		this.parentNortGuid = parentNortGuid;
	}
	public RelationshipNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(RelationshipNode parentNode) {
		this.parentNode = parentNode;
	}
	public List<RelationshipNode> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(List<RelationshipNode> children) {
		this.childNodes = children;
	}
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getStartDateStr() {
		return startDateStr;
	}
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	public String getEndDateStr() {
		return endDateStr;
	}
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	public boolean isRootNode() {
		return isRootNode;
	}
	public void setRootNode(boolean rootNode) {
		this.isRootNode = rootNode;
	}
	public String getDocumentGuid() {
		return documentGuid;
	}
	public void setDocumentGuid(String documentGuid) {
		this.documentGuid = documentGuid;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public boolean getPubTaggedHeadingExists() {
		return pubTaggedHeadingExists;
	}
	public void setPubTaggedHeadingExists(boolean pubTaggedHeadingExists) {
		this.pubTaggedHeadingExists = pubTaggedHeadingExists;
	}
	public List<String> getViews() {
		return views;
	}
	public void setViews(List<String> views) {
		this.views = views;
	}
	
	public String getTocHierarchy() {
		Stack<String> stack = new Stack<String>();
		getParentLabels(parentNode, stack);
		
		StringBuffer buffer = new StringBuffer();
		while(!stack.empty()) {
			buffer.append(stack.pop());
			buffer.append("|");
		}
		return buffer.toString();
		
	}
	
	private void getParentLabels(RelationshipNode parent, Stack<String> stack) {
		if(parent != null) {
			stack.push(parent.getLabel());
			getParentLabels(parent.getParentNode(), stack);
		}
	}
	
	public boolean isDeletedNode() {
		int deletedViewCount = 0;
		for(String view : views) {
			if(view.matches("(^DELER_[a-zA-z0-9_\\-]+)|([a-zA-z0-9_\\-]+DEL$)")) {
				deletedViewCount++;
			}
		}
		
		int viewCount = views.size();
		return viewCount != 0 && deletedViewCount == viewCount;
	}
	
	@Override
	public int compareTo(RelationshipNode o) {
		Double node1 = this.getRank();
		Double node2 = o.getRank();

		if (node1 > node2 ) {
			return 1;
		} else if (node1 < node2) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childNodes == null) ? 0 : childNodes.hashCode());
		result = prime * result
				+ ((documentGuid == null) ? 0 : documentGuid.hashCode());
		result = prime * result
				+ ((endDateStr == null) ? 0 : endDateStr.hashCode());
		result = prime * result + (isRootNode ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((nodeType == null) ? 0 : nodeType.hashCode());
		result = prime * result
				+ ((nortGuid == null) ? 0 : nortGuid.hashCode());
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.hashCode());
		result = prime * result
				+ ((parentNortGuid == null) ? 0 : parentNortGuid.hashCode());
		long temp;
		temp = Double.doubleToLongBits(rank);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((startDateStr == null) ? 0 : startDateStr.hashCode());
		result = prime * result + ((views == null) ? 0 : views.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationshipNode other = (RelationshipNode) obj;
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
		if (Double.doubleToLongBits(rank) != Double
				.doubleToLongBits(other.rank))
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
