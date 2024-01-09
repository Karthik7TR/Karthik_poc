package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.exception.ExpectedProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

/**
 * Implementors of this interface are responsible for interacting with ProView
 * and returning any relevant information (success, failure, response messages,
 * etc) to the caller.
 *
 * <a href="https://thehub.thomsonreuters.com/docs/DOC-63763">ProView Publishing REST API</a>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris
 *         Schwartz</a> u0081674
 *
 */
public interface ProviewClient {
    void setProviewHostname(String hostname) throws UnknownHostException;

    void setProviewHost(InetAddress host);

    /* proview group */
    String getAllProviewGroups(String publisher) throws ProviewException;

    String getProviewGroupById(String groupId) throws ProviewException;

    String getProviewGroupInfo(String groupId, String groupVersion) throws ProviewException;

    String createGroup(String groupId, String groupVersion, String requestBody)
        throws ProviewException, UnsupportedEncodingException;

    String promoteGroup(String groupId, String groupVersion) throws ProviewException;

    String removeGroup(String groupId, String groupVersion) throws ProviewException;

    String deleteGroup(String groupId, String groupVersion) throws ProviewException;

    @NotNull
    String getTitleInfo(@NotNull String titleId, @NotNull String version) throws ProviewException;

    /* proview list */
    String getAllPublishedTitles(String publisher) throws ProviewException;
    List<ProviewTitleReportInfo> getAllPublishedTitlesJson(String publisher) throws ProviewException;

    String getSinglePublishedTitle(String fullyQualifiedTitleId) throws ProviewException;

    String getSingleTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException;

    /**
     * Get all title publications with provided status.
     * @param fullyQualifiedTitleId
     * @param status
     * @return
     * @throws ProviewException
     */
    String getTitleInfosByStatus(@NotNull String fullyQualifiedTitleId, @NotNull String status) throws ProviewException;

    String publishTitle(String fullyQualifiedTitleId, String versionNumber, File eBook) throws ProviewException;

    HttpStatus promoteTitle(String fullyQualifiedTitleId, String eBookVersionNumber) throws ProviewException, ExpectedProviewException;

    HttpStatus removeTitle(String fullyQualifiedTitleId, String eBookVersionNumber) throws ProviewException;

    HttpStatus deleteTitle(String fullyQualifiedTitleId, String eBookVersionNumber) throws ProviewException;

    HttpStatus changeTitleVersionToSuperseded(String fullyQualifiedTitleId, String eBookVersionNumber);

    // String getStatusByVersion(String fullyQualifiedTitleId, String version) throws Exception;

    // String getPublishingStatus(String fullyQualifiedTitleId) throws ProviewException;
}
