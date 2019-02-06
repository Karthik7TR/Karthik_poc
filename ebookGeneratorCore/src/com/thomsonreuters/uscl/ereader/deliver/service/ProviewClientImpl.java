package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * This class is responsible for interacting with ProView via their REST interface.
 *
 * @author u0081674
 *
 */
public class ProviewClientImpl implements ProviewClient {
    private static final Logger LOG = LogManager.getLogger(ProviewClientImpl.class);
    private static final String TITLE_ID = "titleId";

    public static final String PROVIEW_HOST_PARAM = "proviewHost";
    private RestTemplate restTemplate;
    private InetAddress proviewHost;

    private String allGroupsUriTemplate;
    private String singleGroupUriTemplate;
    private String getGroupUriTemplate;
    private String createGroupUriTemplate;
    private String promoteGroupStatusUriTemplate;
    private String removeGroupStatusUriTemplate;
    private String deleteGroupUriTemplate;

    private String getTitlesUriTemplate;
    private String getTitleInfoUriTemplate;
    private String singleTitleTemplate;
    private String singleTitleByVersionUriTemplate;
    private String publishTitleUriTemplate;
    private String promoteTitleUriTemplate;
    private String removeTitleUriTemplate;
    private String deleteTitleUriTemplate;
    private String getTitlesByStatusTemplate;

    private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
    private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

    /*----------------------ProviewGroup operations--------------------------*/

    /**
     * Request will get group definition
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String getAllProviewGroups() throws ProviewException {
        String proviewResponse = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());

            proviewResponse = restTemplate.execute(
                allGroupsUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.debug(e);
            throw new ProviewException(e.getMessage());
        }

        return proviewResponse;
    }

    /**
     * Request will get all versions of group definition by Id
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String getProviewGroupById(final String groupId) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();

        final String proviewResponse = restTemplate.execute(
            singleGroupUriTemplate,
            HttpMethod.GET,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        return proviewResponse;
    }

    /**
     * Request will get group definition by version
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String getProviewGroupInfo(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);
        urlParameters.put("groupVersionNumber", groupVersion);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();

        final String proviewResponse = restTemplate.execute(
            getGroupUriTemplate,
            HttpMethod.GET,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        return proviewResponse;
    }

    @Override
    public String createGroup(final String groupId, final String groupVersion, final String requestBody)
        throws ProviewException, UnsupportedEncodingException {
        final InputStream requestBodyStream = new ByteArrayInputStream(requestBody.getBytes("UTF-8"));
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);
        urlParameters.put("groupVersionNumber", groupVersion);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();
        proviewXMLRequestCallback.setRequestInputStream(requestBodyStream);

        final String proviewResponse = restTemplate.execute(
            createGroupUriTemplate,
            HttpMethod.PUT,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        return proviewResponse;
    }

    /**
     * This request will update Group status to promote
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);
        urlParameters.put("groupVersionNumber", groupVersion);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();

        final String proviewResponse = restTemplate.execute(
            promoteGroupStatusUriTemplate,
            HttpMethod.PUT,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        return proviewResponse;
    }

    /**
     * This request will update Group status to removed
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String removeGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);
        urlParameters.put("groupVersionNumber", groupVersion);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();

        final String proviewResponse = restTemplate.execute(
            removeGroupStatusUriTemplate,
            HttpMethod.PUT,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        return proviewResponse;
    }

    /**
     * Request will delete group
     *
     * @param groupDefinition
     * @return
     * @throws ProviewException
     */
    @Override
    public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put("groupId", groupId);
        urlParameters.put("groupVersionNumber", groupVersion);

        final ProviewXMLRequestCallback proviewXMLRequestCallback =
            proviewRequestCallbackFactory.getXMLRequestCallback();

        final String proviewResponse = restTemplate.execute(
            deleteGroupUriTemplate,
            HttpMethod.DELETE,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            urlParameters);

        // to-do response could be "status cannot be changed from removed to removed"
        return proviewResponse;
    }

    @NotNull
    @Override
    public String getTitleInfo(@NotNull final String titleId, @NotNull final String version) throws ProviewException {
        try {
            LOG.debug("Proview host: " + proviewHost.getHostName());
            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            urlParameters.put(TITLE_ID, titleId);
            urlParameters.put("eBookVersionNumber", version);
            return restTemplate.execute(
                getTitleInfoUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.error("getTitleInfo fails (titleId=" + titleId + "; version=" + version + ")", e);
            throw new ProviewException(e.getMessage(), e);
        }
    }

    /*-----------------------Proview Title operations-----------------------------*/

    @Override
    public String getAllPublishedTitles() throws ProviewException {
        String response = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            LOG.debug("Proview host: " + proviewHost.getHostName());
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            response = restTemplate.execute(
                getTitlesUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.debug(e);
            throw new ProviewException(e.getMessage());
        }

        return response;
    }

    @Override
    public String getSinglePublishedTitle(final String fullyQualifiedTitleId) throws ProviewException {
        String response = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            LOG.debug("Proview host: " + proviewHost.getHostName());
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
            response = restTemplate.execute(
                singleTitleTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.debug(e);
            throw new ProviewException(e.getMessage());
        }

        return response;
    }

    @Override
    public String getSingleTitleInfoByVersion(final String fullyQualifiedTitleId, final String version)
        throws ProviewException {
        String response = null;
        try {
            if (StringUtils.isBlank(fullyQualifiedTitleId) && StringUtils.isBlank(version)) {
                throw new IllegalArgumentException(
                    "Cannot get publishing status for titleId: "
                        + fullyQualifiedTitleId
                        + " and version "
                        + version
                        + ". Both titleId and version should be provided.");
            }

            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
            urlParameters.put("eBookVersionNumber", version);
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            response = restTemplate.execute(
                singleTitleByVersionUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getXMLRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.debug(e);
            throw new ProviewException(e.getMessage());
        }
        return response;
    }

    @Override
    public String getTitleInfosByStatus(@NotNull final String fullyQualifiedTitleId, @NotNull final String status)
        throws ProviewException {
        String response = null;
        try {
            if (StringUtils.isBlank(fullyQualifiedTitleId) || StringUtils.isBlank(status)) {
                throw new IllegalArgumentException(
                    "Cannot get publishing status for titleId: "
                        + fullyQualifiedTitleId
                        + " with status"
                        + status
                        + ". Both parameters should be provided.");
            }

            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
            urlParameters.put("status", status);
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            response = restTemplate.execute(
                getTitlesByStatusTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getXMLRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            LOG.debug(e);
            throw new ProviewException(e.getMessage());
        }
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#publishTitle (java.lang.String, java.lang.String,
     * java.io.File)
     */
    @Override
    public String publishTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber, final File eBook)
        throws ProviewException {
        if (StringUtils.isBlank(fullyQualifiedTitleId)) {
            throw new IllegalArgumentException(
                "fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
        }
        if (StringUtils.isBlank(eBookVersionNumber)) {
            throw new IllegalArgumentException(
                "eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
        }

        String proviewResponse = null;

        try (FileInputStream ebookInputStream = new FileInputStream(eBook)) {
            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
            urlParameters.put("eBookVersionNumber", eBookVersionNumber);

            final ProviewRequestCallback proviewRequestCallback =
                proviewRequestCallbackFactory.getStreamRequestCallback();
            proviewRequestCallback.setEbookInputStream(ebookInputStream);

            proviewResponse = restTemplate.execute(
                publishTitleUriTemplate,
                HttpMethod.PUT,
                proviewRequestCallback,
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return proviewResponse;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#promoteTitle (java.lang.String, java.lang.String)
     */
    @Override
    public HttpStatus promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException {
        if (StringUtils.isBlank(fullyQualifiedTitleId)) {
            throw new IllegalArgumentException(
                "fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
        }
        if (StringUtils.isBlank(eBookVersionNumber)) {
            throw new IllegalArgumentException(
                "eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
        }

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
        urlParameters.put("eBookVersionNumber", eBookVersionNumber);

        final ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

        final ClientHttpResponse proviewResponse = restTemplate.execute(
            promoteTitleUriTemplate,
            HttpMethod.PUT,
            proviewRequestCallback,
            proviewResponseExtractorFactory.getSimpleResponseExtractor(),
            urlParameters);

        return getStatusCode(proviewResponse);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#removeTitle (java.lang.String, java.lang.String)
     */
    @Override
    public HttpStatus removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException {
        if (StringUtils.isBlank(fullyQualifiedTitleId)) {
            throw new IllegalArgumentException(
                "fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
        }
        if (StringUtils.isBlank(eBookVersionNumber)) {
            throw new IllegalArgumentException(
                "eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
        }

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
        urlParameters.put("eBookVersionNumber", eBookVersionNumber);

        final ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

        final ClientHttpResponse proviewResponse = restTemplate.execute(
            removeTitleUriTemplate,
            HttpMethod.PUT,
            proviewRequestCallback,
            proviewResponseExtractorFactory.getSimpleResponseExtractor(),
            urlParameters);
        return getStatusCode(proviewResponse);
    }

    @SneakyThrows
    private HttpStatus getStatusCode(ClientHttpResponse proviewResponse) {
        return proviewResponse.getStatusCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#deleteTitle (java.lang.String, java.lang.String)
     */
    @Override
    public HttpStatus deleteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException {
        if (StringUtils.isBlank(fullyQualifiedTitleId)) {
            throw new IllegalArgumentException(
                "fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
        }
        if (StringUtils.isBlank(eBookVersionNumber)) {
            throw new IllegalArgumentException(
                "eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
        }

        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
        urlParameters.put("eBookVersionNumber", eBookVersionNumber);

        final ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

        final ClientHttpResponse proviewResponse = restTemplate.execute(
            deleteTitleUriTemplate,
            HttpMethod.DELETE,
            proviewRequestCallback,
            proviewResponseExtractorFactory.getSimpleResponseExtractor(),
            urlParameters);

        return getStatusCode(proviewResponse);
    }

    /**
     * Compose the search request body.
     *
     * @param writer
     * @param name
     * @param value
     * @throws XMLStreamException
     */
    protected void writeElement(final XMLStreamWriter writer, final String name, final Object value)
        throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement(name);
            writer.writeCharacters(value.toString().trim());
            writer.writeEndElement();
        }
    }

    /**
     * Allows for the dynamic setting of this host name "on the fly".
     *
     * @param host the new host name to use. For example a production or test host.
     */
    @Override
    public void setProviewHostname(final String hostname) throws UnknownHostException {
        setProviewHost(InetAddress.getByName(hostname));
    }

    @Override
    public void setProviewHost(final InetAddress host) {
        proviewHost = host;
    }

    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Required
    public void setPublishTitleUriTemplate(final String publishTitleUriTemplate) {
        this.publishTitleUriTemplate = publishTitleUriTemplate;
    }

    @Required
    public void setGetTitlesUriTemplate(final String getTitlesUriTemplate) {
        this.getTitlesUriTemplate = getTitlesUriTemplate;
    }

    @Required
    public void setGetTitleInfoUriTemplate(final String getTitleInfoUriTemplate) {
        this.getTitleInfoUriTemplate = getTitleInfoUriTemplate;
    }

    @Required
    public String getCreateGroupUriTemplate() {
        return createGroupUriTemplate;
    }

    @Required
    public void setCreateGroupUriTemplate(final String createGroupUriTemplate) {
        this.createGroupUriTemplate = createGroupUriTemplate;
    }

    @Required
    public void setProviewRequestCallbackFactory(final ProviewRequestCallbackFactory proviewRequestCallbackFactory) {
        this.proviewRequestCallbackFactory = proviewRequestCallbackFactory;
    }

    @Required
    public void setProviewResponseExtractorFactory(
        final ProviewResponseExtractorFactory proviewResponseExtractorFactory) {
        this.proviewResponseExtractorFactory = proviewResponseExtractorFactory;
    }

    public String getDeleteTitleUriTemplate() {
        return deleteTitleUriTemplate;
    }

    @Required
    public void setDeleteTitleUriTemplate(final String deleteTitleUriTemplate) {
        this.deleteTitleUriTemplate = deleteTitleUriTemplate;
    }

    public String getRemoveTitleUriTemplate() {
        return removeTitleUriTemplate;
    }

    @Required
    public void setRemoveTitleUriTemplate(final String removeTitleUriTemplate) {
        this.removeTitleUriTemplate = removeTitleUriTemplate;
    }

    public String getPromoteTitleUriTemplate() {
        return promoteTitleUriTemplate;
    }

    @Required
    public void setPromoteTitleUriTemplate(final String promoteTitleUriTemplate) {
        this.promoteTitleUriTemplate = promoteTitleUriTemplate;
    }

    public String getRemoveGroupStatusUriTemplate() {
        return removeGroupStatusUriTemplate;
    }

    @Required
    public void setRemoveGroupStatusUriTemplate(final String updateGroupStatusUriTemplate) {
        removeGroupStatusUriTemplate = updateGroupStatusUriTemplate;
    }

    public String getGetGroupUriTemplate() {
        return getGroupUriTemplate;
    }

    @Required
    public void setGetGroupUriTemplate(final String getGroupUriTemplate) {
        this.getGroupUriTemplate = getGroupUriTemplate;
    }

    public String getPromoteGroupStatusUriTemplate() {
        return promoteGroupStatusUriTemplate;
    }

    @Required
    public void setPromoteGroupStatusUriTemplate(final String promoteGroupStatusUriTemplate) {
        this.promoteGroupStatusUriTemplate = promoteGroupStatusUriTemplate;
    }

    public String getDeleteGroupUriTemplate() {
        return deleteGroupUriTemplate;
    }

    @Required
    public void setDeleteGroupUriTemplate(final String deleteGroupUriTemplate) {
        this.deleteGroupUriTemplate = deleteGroupUriTemplate;
    }

    public String getAllGroupsUriTemplate() {
        return allGroupsUriTemplate;
    }

    @Required
    public void setAllGroupsUriTemplate(final String allGroupsUriTemplate) {
        this.allGroupsUriTemplate = allGroupsUriTemplate;
    }

    public String getSingleTitleTemplate() {
        return singleTitleTemplate;
    }

    @Required
    public void setSingleTitleTemplate(final String singleTitleTemplate) {
        this.singleTitleTemplate = singleTitleTemplate;
    }

    public String getSingleTitleByVersionUriTemplate() {
        return singleTitleByVersionUriTemplate;
    }

    @Required
    public void setSingleTitleByVersionUriTemplate(final String singleTitleByVersionUriTemplate) {
        this.singleTitleByVersionUriTemplate = singleTitleByVersionUriTemplate;
    }

    public String getSingleGroupUriTemplate() {
        return singleGroupUriTemplate;
    }

    @Required
    public void setSingleGroupUriTemplate(final String singleGroupUriTemplate) {
        this.singleGroupUriTemplate = singleGroupUriTemplate;
    }

    @Required
    public void setGetTitlesByStatusTemplate(final String getTitlesByStatusTemplate) {
        this.getTitlesByStatusTemplate = getTitlesByStatusTemplate;
    }
}
