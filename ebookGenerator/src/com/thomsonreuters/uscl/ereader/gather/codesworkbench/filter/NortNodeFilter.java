/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;

public class NortNodeFilter {
	private List<RelationshipNode> rootNodes = new ArrayList<RelationshipNode>();
	private List<RelationshipNode> markedForRemovalNodes = new ArrayList<RelationshipNode>();
	private List<RelationshipNode> removedNodes = new ArrayList<RelationshipNode>();
	
	public NortNodeFilter(List<RelationshipNode> rootNodes) {
		this.rootNodes = rootNodes;
	}
	
	public List<RelationshipNode> filterEmptyNodes() {
		for(RelationshipNode node : rootNodes) {
			checkNode(node);
		}
		
		for(RelationshipNode node : markedForRemovalNodes) {
			removeNodeAndCheckParent(node);
		}
		
		return removedNodes;
	}
	
	private void checkNode(RelationshipNode node) {
		if(node.getChildNodes().size() == 0 && StringUtils.isEmpty(node.getDocumentGuid())) {
			markedForRemovalNodes.add(node);
		} else if (node.getChildNodes().size() > 0) {
			for(RelationshipNode childNode : node.getChildNodes()) {
				checkNode(childNode);
			}
		}
	}
	
	private void removeNodeAndCheckParent(RelationshipNode node) {
		if(node.getChildNodes().size() == 0 && StringUtils.isEmpty(node.getDocumentGuid())) {
			RelationshipNode parentNode = node.getParentNode();
			
			// Skip check when on root node, root node will not be removed
			if(parentNode != null) {
				List<RelationshipNode> childNodes = parentNode.getChildNodes();
				if(childNodes.remove(node)) {
					removedNodes.add(node);
					removeNodeAndCheckParent(parentNode); 
				} else {
					System.err.println("Could not remove " + node.getLabel());
				}
			}
		}
	}
	
}
