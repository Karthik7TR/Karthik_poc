package com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import org.springframework.util.StringUtils;

public class NortNodeFilter {
    private List<RelationshipNode> rootNodes = new ArrayList<>();
    private List<RelationshipNode> markedForRemovalNodes = new ArrayList<>();
    private List<RelationshipNode> removedNodes = new ArrayList<>();

    private List<RelationshipNode> wlNotificationNodes = new ArrayList<>();

    private static final String NO_WLN_PUB = "no WL pubtag";

    public NortNodeFilter(final List<RelationshipNode> rootNodes) {
        this.rootNodes = rootNodes;
    }

    public List<RelationshipNode> getWLNotificationNodes() {
        return wlNotificationNodes;
    }

    public List<RelationshipNode> filterEmptyNodes() {
        for (final RelationshipNode node : rootNodes) {
            checkNode(node);
        }

        for (final RelationshipNode node : markedForRemovalNodes) {
            removeNodeAndCheckParent(node);
        }

        return removedNodes;
    }

    private void checkNode(final RelationshipNode node) {
        if (node.getLabel() != null
            && node.getLabel().equalsIgnoreCase(NO_WLN_PUB)
            && (node.getChildNodes().size() > 0 || !StringUtils.isEmpty(node.getDocumentGuid()))) {
            wlNotificationNodes.add(node);
        }

        if (node.getChildNodes().size() == 0
            && StringUtils.isEmpty(node.getDocumentGuid())
            && !node.getPubTaggedHeadingExists()) {
            // Remove leaf node that does not contain Document GUID.
            // Do not remove node if pub-tagged-heading is marked true on node.
            // This indicates for blank document to be placed in the book.
            markedForRemovalNodes.add(node);
        } else if (node.getChildNodes().size() > 0) {
            for (final RelationshipNode childNode : node.getChildNodes()) {
                checkNode(childNode);
            }
        }
    }

    private void removeNodeAndCheckParent(final RelationshipNode node) {
        if (node.getChildNodes().size() == 0 && StringUtils.isEmpty(node.getDocumentGuid())) {
            final RelationshipNode parentNode = node.getParentNode();

            // Skip check when on root node, root node will not be removed
            if (parentNode != null) {
                final List<RelationshipNode> childNodes = parentNode.getChildNodes();
                if (childNodes.remove(node)) {
                    if (wlNotificationNodes.contains(node)) {
                        wlNotificationNodes.remove(node);
                    }
                    removedNodes.add(node);
                    removeNodeAndCheckParent(parentNode);
                } else {
                    System.err.println("Could not remove " + node.getLabel());
                }
            }
        }
    }
}
