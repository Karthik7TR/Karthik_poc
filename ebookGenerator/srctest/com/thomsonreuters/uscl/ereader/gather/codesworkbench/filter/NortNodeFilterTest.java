/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;

public class NortNodeFilterTest {
	private NortNodeFilter filter = null;
	private List<RelationshipNode> nodes = null;
	
	@Before
	public void setup() {
		nodes = new ArrayList<RelationshipNode>();
		filter = new NortNodeFilter(nodes);
	}
	
	@Test
	public void noEmptyNodesTest() {
		nodes.add(createNodes(1, true, false));
		List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
		Assert.assertEquals(0, emptyNodes.size());
		List<RelationshipNode> wlNodes =  filter.getWLNotificationNodes();
		Assert.assertEquals(0, wlNodes.size());
	}
	
	@Test
	public void wlNodesTest() {
		RelationshipNode rootNode = createNodes(1, true,false);
		rootNode.setLabel("no WL pubtag");
		nodes.add(rootNode);
		 filter.filterEmptyNodes();
		 List<RelationshipNode> wlNodes =  filter.getWLNotificationNodes();
		Assert.assertEquals(1, wlNodes.size());
	}
	
	@Test
	public void noWLNodesTest() {
		RelationshipNode rootNode = createNodes(1, false, false);
		rootNode.setLabel("no WL pubtag");
		nodes.add(rootNode);
		 filter.filterEmptyNodes();
		 List<RelationshipNode> wlNodes =  filter.getWLNotificationNodes();
		Assert.assertEquals(1, wlNodes.size());
	}
	
	@Test
	public void threeWLNodesTest() {
		int numRemovedNodes = 3;
		RelationshipNode rootNode = createNodes(3, false, true);
		nodes.add(rootNode);
		List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
		 List<RelationshipNode> wlNodes =  filter.getWLNotificationNodes();
		Assert.assertEquals(0, wlNodes.size());
		Assert.assertEquals(numRemovedNodes, emptyNodes.size());
		
	}
	
	@Test
	public void oneEmptyNodeTest() {
		int numRemovedNodes = 1;
		nodes.add(createNodes(numRemovedNodes, false, false));
		List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
		Assert.assertEquals(numRemovedNodes, emptyNodes.size());
	}
	
	@Test
	public void fiveEmptyNodeTest() {
		int numRemovedNodes = 5;
		nodes.add(createNodes(numRemovedNodes, false, false));
		List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
		Assert.assertEquals(numRemovedNodes, emptyNodes.size());
		List<RelationshipNode> wlNodes =  filter.getWLNotificationNodes();
		Assert.assertEquals(0, wlNodes.size());
	}
	
	private RelationshipNode createNodes(int numChildren, boolean leafContainsDocument, boolean wlNode) {
		RelationshipNode rootNode = new RelationshipNode();
		rootNode.setLabel("Root node");
		rootNode.setNortGuid("0");
		rootNode.setRootNode(true);
		rootNode.getChildNodes().addAll(generateRandomNumberNodesWithDocument(rootNode));
		
		RelationshipNode parentNode = rootNode;
		for(int i = 0; i < numChildren; i++) {
			RelationshipNode node = new RelationshipNode();
			rootNode.setLabel("Child Node " + i);
			rootNode.setNortGuid("Child Guid " + i);
			parentNode.getChildNodes().add(node);
			node.setParentNode(parentNode);
			
			if (wlNode){
				node.setLabel("no WL pubtag");
				wlNode = false;
			}
			
			if(leafContainsDocument && (numChildren - 1) == i) {
				node.setDocumentGuid("Contains Document");
			}
			parentNode = node;
		}
		
		return rootNode;
	}
	
	private List<RelationshipNode> generateRandomNumberNodesWithDocument(RelationshipNode parentNode) {
		List<RelationshipNode> nodes = new ArrayList<RelationshipNode>();
		Random ran = new Random();
		
		// Generate upto 9 random nodes
		int randomNumber = ran.nextInt(10);
		for(int i = 0; i < randomNumber; i++) {
			RelationshipNode randomNode = new RelationshipNode();
			randomNode.setLabel("Random Node Label" + i);
			randomNode.setNortGuid("Random Node GUID" + i);
			randomNode.setDocumentGuid("Random Node Doc" + i );
			randomNode.setParentNode(parentNode);
			nodes.add(randomNode);
		}
		
		return nodes;
	}
}
