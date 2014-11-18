/*
 * Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain;

import java.util.ArrayList;
import java.util.List;

public class RelationshipNode implements Comparable<RelationshipNode> {
	private String nortGuid;
	private String parentNortGuid;
	private RelationshipNode parentNode;
	private List<RelationshipNode> childNodes = new ArrayList<RelationshipNode>();
	private int rank;
	private String label;
	private String startDateStr;
	private String endDateStr;
	private boolean isRootNode = false;
	private String documentGuid;
	private String nodeType;
	
	
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
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
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

	@Override
	public int compareTo(RelationshipNode o) {
		Integer node1 = this.getRank();
		Integer node2 = o.getRank();

		if (node1 > node2 ) {
			return 1;
		} else if (node1 < node2) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
