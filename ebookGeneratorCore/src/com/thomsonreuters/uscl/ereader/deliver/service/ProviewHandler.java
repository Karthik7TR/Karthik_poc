package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ExpectedProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

/**
 * Implementors of this interface are responsible for interacting with ProView and returning any relevant information (success,
 * failure, response messages, etc) to the caller.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 *
 */
public interface ProviewHandler {
    /* ProView group */
    Map<String, ProviewGroupContainer> getAllProviewGroupInfo() throws ProviewException;

    ProviewGroupContainer getProviewGroupContainerById(String groupId) throws ProviewException;

    List<GroupDefinition> getGroupDefinitionsById(String groupId) throws ProviewException;

    GroupDefinition getGroupDefinitionByVersion(String groupId, long groupVersion) throws ProviewException;

    List<ProviewGroup> getAllLatestProviewGroupInfo() throws ProviewException;

    List<ProviewGroup> getAllLatestProviewGroupInfo(Map<String, ProviewGroupContainer> groupMap)
        throws ProviewException;

    String createGroup(GroupDefinition groupDefinition) throws ProviewException, UnsupportedEncodingException;

    String promoteGroup(String groupId, String groupVersion) throws ProviewException;

    String removeGroup(String groupId, String groupVersion) throws ProviewException;

    String deleteGroup(String groupId, String groupVersion) throws ProviewException;

    /* ProView Title */
    Map<String, ProviewTitleContainer> getAllProviewTitleInfo() throws ProviewException;

    Map<String, ProviewTitleContainer> getTitlesWithUnitedParts() throws ProviewException;

    ProviewTitleContainer getProviewTitleContainer(String fullyQualifiedTitleId) throws ProviewException;

    List<ProviewTitleInfo> getAllLatestProviewTitleInfo() throws ProviewException;

    List<ProviewTitleInfo> getAllLatestProviewTitleInfo(Map<String, ProviewTitleContainer> titleMap)
        throws ProviewException;

    ProviewTitleInfo getLatestPublishedProviewTitleInfo(String fullyQualifiedTitleId) throws ProviewException;

    ProviewTitleInfo getLatestProviewTitleInfo(String fullyQualifiedTitleId) throws ProviewException;

    String getTitleIdCaseSensitiveForVersion(String fullyQualifiedTitleId, String version);

    // ProviewTitleInfo getProviewTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException;

    List<GroupDetails> getSingleTitleGroupDetails(String fullyQualifiedTitleId) throws ProviewException;

    boolean isTitleInProview(String fullyQualifiedTitleId) throws ProviewException;

    boolean hasTitleIdBeenPublished(String fullyQualifiedTitleId) throws ProviewException;

    String publishTitle(String fullyQualifiedTitleId, Version version, File eBook) throws ProviewException;

    boolean promoteTitle(String fullyQualifiedTitleId, String eBookVersionNumber) throws ProviewException, ExpectedProviewException;

    void markTitleSuperseded(String fullyQualifiedTitleId) throws ProviewException;

    boolean removeTitle(String fullyQualifiedTitleId, Version version) throws ProviewException;

    boolean deleteTitle(String fullyQualifiedTitleId, Version version) throws ProviewException;
}
