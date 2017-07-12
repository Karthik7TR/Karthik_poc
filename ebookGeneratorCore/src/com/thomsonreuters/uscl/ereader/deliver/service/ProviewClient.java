package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import org.jetbrains.annotations.NotNull;

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
public interface ProviewClient
{
    void setProviewHostname(String hostname) throws UnknownHostException;

    void setProviewHost(InetAddress host);

    /* proview group */
    String getAllProviewGroups() throws ProviewException;

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
    String getAllPublishedTitles() throws ProviewException;

    String getSinglePublishedTitle(String fullyQualifiedTitleId) throws ProviewException;

    String getSingleTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException;

    String publishTitle(String fullyQualifiedTitleId, String versionNumber, File eBook)
        throws ProviewException;

    String promoteTitle(String fullyQualifiedTitleId, String eBookVersionNumber)
        throws ProviewException;

    String removeTitle(String fullyQualifiedTitleId, String eBookVersionNumber)
        throws ProviewException;

    String deleteTitle(String fullyQualifiedTitleId, String eBookVersionNumber)
        throws ProviewException;

    // String getStatusByVersion(String fullyQualifiedTitleId, String version) throws Exception;

    // String getPublishingStatus(String fullyQualifiedTitleId) throws ProviewException;
}
