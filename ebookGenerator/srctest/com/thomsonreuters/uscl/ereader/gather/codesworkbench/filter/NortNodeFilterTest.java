package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class NortNodeFilterTest {
    private NortNodeFilter filter;
    private List<RelationshipNode> nodes;

    @Before
    public void setup() {
        nodes = new ArrayList<>();
        filter = new NortNodeFilter(nodes);
    }

    @Test
    public void noEmptyNodesTest() {
        nodes.add(createNodes(1, true, false));
        final List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
        Assert.assertEquals(0, emptyNodes.size());
        final List<RelationshipNode> wlNodes = filter.getWLNotificationNodes();
        Assert.assertEquals(0, wlNodes.size());
    }

    @Test
    public void wlNodesTest() {
        final RelationshipNode rootNode = createNodes(1, true, false);
        rootNode.setLabel("no WL pubtag");
        nodes.add(rootNode);
        filter.filterEmptyNodes();
        final List<RelationshipNode> wlNodes = filter.getWLNotificationNodes();
        Assert.assertEquals(1, wlNodes.size());
    }

    @Test
    public void noWLNodesTest() {
        final RelationshipNode rootNode = createNodes(1, false, false);
        rootNode.setLabel("no WL pubtag");
        nodes.add(rootNode);
        filter.filterEmptyNodes();
        final List<RelationshipNode> wlNodes = filter.getWLNotificationNodes();
        Assert.assertEquals(1, wlNodes.size());
    }

    @Test
    public void threeWLNodesTest() {
        final int numRemovedNodes = 3;
        final RelationshipNode rootNode = createNodes(3, false, true);
        nodes.add(rootNode);
        final List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
        final List<RelationshipNode> wlNodes = filter.getWLNotificationNodes();
        Assert.assertEquals(0, wlNodes.size());
        Assert.assertEquals(numRemovedNodes, emptyNodes.size());
    }

    @Test
    public void oneEmptyNodeTest() {
        final int numRemovedNodes = 1;
        nodes.add(createNodes(numRemovedNodes, false, false));
        final List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
        Assert.assertEquals(numRemovedNodes, emptyNodes.size());
    }

    @Test
    public void fiveEmptyNodeTest() {
        final int numRemovedNodes = 5;
        nodes.add(createNodes(numRemovedNodes, false, false));
        final List<RelationshipNode> emptyNodes = filter.filterEmptyNodes();
        Assert.assertEquals(numRemovedNodes, emptyNodes.size());
        final List<RelationshipNode> wlNodes = filter.getWLNotificationNodes();
        Assert.assertEquals(0, wlNodes.size());
    }

    private RelationshipNode createNodes(final int numChildren, final boolean leafContainsDocument, boolean wlNode) {
        final RelationshipNode rootNode = new RelationshipNode();
        rootNode.setLabel("Root node");
        rootNode.setNortGuid("0");
        rootNode.setRootNode(true);
        rootNode.getChildNodes().addAll(generateRandomNumberNodesWithDocument(rootNode));

        RelationshipNode parentNode = rootNode;
        for (int i = 0; i < numChildren; i++) {
            final RelationshipNode node = new RelationshipNode();
            rootNode.setLabel("Child Node " + i);
            rootNode.setNortGuid("Child Guid " + i);
            parentNode.getChildNodes().add(node);
            node.setParentNode(parentNode);

            if (wlNode) {
                node.setLabel("no WL pubtag");
                wlNode = false;
            }

            if (leafContainsDocument && (numChildren - 1) == i) {
                node.setDocumentGuid("Contains Document");
            }
            parentNode = node;
        }

        return rootNode;
    }

    private List<RelationshipNode> generateRandomNumberNodesWithDocument(final RelationshipNode parentNode) {
        final List<RelationshipNode> randNodes = new ArrayList<>();
        final Random ran = new Random();

        // Generate upto 9 random nodes
        final int randomNumber = ran.nextInt(10);
        for (int i = 0; i < randomNumber; i++) {
            final RelationshipNode randomNode = new RelationshipNode();
            randomNode.setLabel("Random Node Label" + i);
            randomNode.setNortGuid("Random Node GUID" + i);
            randomNode.setDocumentGuid("Random Node Doc" + i);
            randomNode.setParentNode(parentNode);
            randNodes.add(randomNode);
        }

        return randNodes;
    }
}
