package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.thomsonreuters.uscl.ereader.common.retry.Retry;
import com.thomsonreuters.uscl.ereader.deliver.exception.ExpectedProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallback;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewRequestCallbackFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewResponseExtractorFactory;
import com.thomsonreuters.uscl.ereader.deliver.rest.ProviewXMLRequestCallback;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class ProviewClientImpl implements ProviewClient {
    private static final String ALL = "/all";
    private static final String TITLE_ID = "titleId";
    private static final String EBOOK_VERSION_NUMBER = "eBookVersionNumber";
    private static final String PROVIEW_HOST_PARAM = "proviewHost";
    private static final String FINAL_TO_FINAL_MESSAGE = "Title status cannot be changed from Final to Final";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP_VERSION_NUMBER = "groupVersionNumber";
    public static final String CHANGE_TITLE_TO_SUPERSEDED_REQUEST_BODY =
        "<title_metadata_update>" +
            "<update>" +
                "<features>" +
                    "<feature name=\"superseded\" value=\"Superseded\" />" +
                "</features>" +
            "</update>" +
        "</title_metadata_update>";
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
    private String getTitlesJsonUriTemplate;
    private String getTitleInfoUriTemplate;
    private String singleTitleTemplate;
    private String singleTitleByVersionUriTemplate;
    private String publishTitleUriTemplate;
    private String promoteTitleUriTemplate;
    private String removeTitleUriTemplate;
    private String deleteTitleUriTemplate;
    private String getTitlesByStatusTemplate;

    @Setter(onMethod_ = {@Required})
    private String modifySingleTitleWithVersionUriTemplate;

    private ProviewRequestCallbackFactory proviewRequestCallbackFactory;
    private ProviewResponseExtractorFactory proviewResponseExtractorFactory;

    /*----------------------ProviewGroup operations--------------------------*/

    /**
     * Request will get group definition
     *
     * @param publisher
     * @return
     * @throws ProviewException
     */
    @Override
    public String getAllProviewGroups(final String publisher) throws ProviewException {
        String proviewResponse = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());

            proviewResponse = restTemplate.execute(
                allGroupsUriTemplate + publisher,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            log.debug(e.getMessage());
            throw new ProviewException(e.getMessage());
        }

        return proviewResponse;
    }

    /**
     * Request will get all versions of group definition by Id
     *
     * @return
     * @throws ProviewException
     */
    @Override
    public String getProviewGroupById(final String groupId) throws ProviewException {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(GROUP_ID, groupId);

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
     * @return
     * @throws ProviewException
     */
    @Override
    public String getProviewGroupInfo(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = setGroupUrlParams(groupId, groupVersion);

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
        final Map<String, String> urlParameters = setGroupUrlParams(groupId, groupVersion);

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
     * @return
     * @throws ProviewException
     */
    @Override
    public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = setGroupUrlParams(groupId, groupVersion);

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
     * @return
     * @throws ProviewException
     */
    @Override
    public String removeGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = setGroupUrlParams(groupId, groupVersion);

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
     * @return
     * @throws ProviewException
     */
    @Override
    public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException {
        final Map<String, String> urlParameters = setGroupUrlParams(groupId, groupVersion);

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
            log.debug("Proview host: " + proviewHost.getHostName());
            final Map<String, String> urlParameters = setTitleVersionUrlParams(titleId, version);
            return restTemplate.execute(
                getTitleInfoUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            log.error("getTitleInfo fails (titleId=" + titleId + "; version=" + version + ")", e);
            throw new ProviewException(e.getMessage(), e);
        }
    }

    /*-----------------------Proview Title operations-----------------------------*/

    @Override
    public String getAllPublishedTitles(final String publisher) throws ProviewException {
        String response = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            log.debug("Proview host: " + proviewHost.getHostName());
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            response = restTemplate.execute(
                getTitlesUriTemplate + publisher + ALL,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            log.debug(e.getMessage());
            throw new ProviewException(e.getMessage());
        }

        return response;
    }

    public List<ProviewTitleReportInfo> getAllPublishedTitlesJson(String publisher)
            throws ProviewException {

        String jsonResponseListString = null;
        List<ProviewTitleReportInfo> lstProviewTitleReportInfo = new ArrayList<ProviewTitleReportInfo>();
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            log.debug("Proview host: " + proviewHost.getHostName());
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            jsonResponseListString = restTemplate.execute(
                    getTitlesJsonUriTemplate + publisher + ALL,
                    HttpMethod.GET,
                    proviewRequestCallbackFactory.getStreamRequestCallback(),
                    proviewResponseExtractorFactory.getResponseExtractor(),
                    urlParameters);
            ObjectMapper mapper = new ObjectMapper();
            final DateFormat df = new SimpleDateFormat("yyyyMMdd");
            mapper.setDateFormat(df);
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
            lstProviewTitleReportInfo = mapper.readValue(jsonResponseListString, new TypeReference<List<ProviewTitleReportInfo>>() {});

        } catch (final Exception e) {
            log.debug(e.getMessage());
            throw new ProviewException(e.getMessage());
        }

        return lstProviewTitleReportInfo;

    }

    @Override
    public String getSinglePublishedTitle(final String fullyQualifiedTitleId) throws ProviewException {
        String response = null;
        try {
            final Map<String, String> urlParameters = new HashMap<>();
            log.debug("Proview host: " + proviewHost.getHostName());
            urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
            urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
            response = restTemplate.execute(
                singleTitleTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getStreamRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            log.debug(e.getMessage());
            throw new ProviewException(e.getMessage());
        }

        return response;
    }

    @Override
    public String getSingleTitleInfoByVersion(final String fullyQualifiedTitleId, final String version)
        throws ProviewException {
        String response = null;
        try {
            validateTitleAndVersion(fullyQualifiedTitleId, version);

            final Map<String, String> urlParameters = setTitleVersionUrlParams(fullyQualifiedTitleId, version);
            response = restTemplate.execute(
                singleTitleByVersionUriTemplate,
                HttpMethod.GET,
                proviewRequestCallbackFactory.getXMLRequestCallback(),
                proviewResponseExtractorFactory.getResponseExtractor(),
                urlParameters);
        } catch (final Exception e) {
            log.debug(e.getMessage());
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
            log.debug(e.getMessage());
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
        validateTitleAndVersion(fullyQualifiedTitleId, eBookVersionNumber);

        String proviewResponse = null;

        try (FileInputStream ebookInputStream = new FileInputStream(eBook)) {
            final Map<String, String> urlParameters = setTitleVersionUrlParams(fullyQualifiedTitleId, eBookVersionNumber);

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
            log.error(e.getMessage(), e);
        }
        return proviewResponse;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#promoteTitle (java.lang.String, java.lang.String)
     */
    @Override
    @Retry(propertyValue = "proview.retry.count",
        exceptions = {ProviewException.class, ProviewRuntimeException.class},
        delayProperty = "proview.retry.delay.ms"
    )
    public HttpStatus promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException, ExpectedProviewException {
        validateTitleAndVersion(fullyQualifiedTitleId, eBookVersionNumber);

        final Map<String, String> urlParameters = setTitleVersionUrlParams(fullyQualifiedTitleId, eBookVersionNumber);

        final ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

        try {
            final ClientHttpResponse proviewResponse = restTemplate.execute(
                    promoteTitleUriTemplate,
                    HttpMethod.PUT,
                    proviewRequestCallback,
                    proviewResponseExtractorFactory.getSimpleResponseExtractor(),
                    urlParameters);
            return getStatusCode(proviewResponse);
        } catch (final ProviewRuntimeException e) {
            log.warn(e.getMessage(), e);
            if (e.getMessage().contains(FINAL_TO_FINAL_MESSAGE)) {
                throw new ExpectedProviewException(e.getMessage(), e);
            } else {
                throw e;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient#removeTitle (java.lang.String, java.lang.String)
     */
    @Override
    public HttpStatus removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException {
        validateTitleAndVersion(fullyQualifiedTitleId, eBookVersionNumber);

        final Map<String, String> urlParameters = setTitleVersionUrlParams(fullyQualifiedTitleId, eBookVersionNumber);

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
    private HttpStatus getStatusCode(final ClientHttpResponse proviewResponse) {
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
        validateTitleAndVersion(fullyQualifiedTitleId, eBookVersionNumber);

        final Map<String, String> urlParameters = setTitleVersionUrlParams(fullyQualifiedTitleId, eBookVersionNumber);

        final ProviewRequestCallback proviewRequestCallback = proviewRequestCallbackFactory.getStreamRequestCallback();

        final ClientHttpResponse proviewResponse = restTemplate.execute(
            deleteTitleUriTemplate,
            HttpMethod.DELETE,
            proviewRequestCallback,
            proviewResponseExtractorFactory.getSimpleResponseExtractor(),
            urlParameters);

        return getStatusCode(proviewResponse);
    }

    @Override
    public HttpStatus changeTitleVersionToSuperseded(final String fullyQualifiedTitleId, final String eBookVersionNumber) {
        validateTitleAndVersion(fullyQualifiedTitleId, eBookVersionNumber);

        final ProviewXMLRequestCallback proviewXMLRequestCallback = proviewRequestCallbackFactory.getXMLRequestCallback();
        try {
            proviewXMLRequestCallback.setRequestInputStream(new ByteArrayInputStream(CHANGE_TITLE_TO_SUPERSEDED_REQUEST_BODY.getBytes("UTF-8")));
        } catch (final UnsupportedEncodingException e) {
            log.warn(e.getMessage(), e);
        }

        restTemplate.execute(
            modifySingleTitleWithVersionUriTemplate,
            HttpMethod.PUT,
            proviewXMLRequestCallback,
            proviewResponseExtractorFactory.getResponseExtractor(),
            setTitleVersionUrlParams(fullyQualifiedTitleId, eBookVersionNumber));

        return HttpStatus.OK;
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

    private Map<String, String> setGroupUrlParams(final String groupId, final String groupVersion) {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(GROUP_ID, groupId);
        urlParameters.put(GROUP_VERSION_NUMBER, groupVersion);
        return urlParameters;
    }

    private Map<String, String> setTitleVersionUrlParams(final String fullyQualifiedTitleId, final String eBookVersionNumber) {
        final Map<String, String> urlParameters = new HashMap<>();
        urlParameters.put(PROVIEW_HOST_PARAM, proviewHost.getHostName());
        urlParameters.put(TITLE_ID, fullyQualifiedTitleId);
        urlParameters.put(EBOOK_VERSION_NUMBER, eBookVersionNumber);
        return urlParameters;
    }

    private void validateTitleAndVersion(final String fullyQualifiedTitleId, final String eBookVersionNumber) {
        if (StringUtils.isBlank(fullyQualifiedTitleId)) {
            throw new IllegalArgumentException(
                "fullyQualifiedTitleId cannot be null or empty, but was [" + fullyQualifiedTitleId + "].");
        }
        if (StringUtils.isBlank(eBookVersionNumber)) {
            throw new IllegalArgumentException(
                "eBookVersionNumber must not be null or empty, but was [" + eBookVersionNumber + "].");
        }
    }

    /**
     * Allows for the dynamic setting of this host name "on the fly".
     *
     * @param hostname the new host name to use. For example a production or test host.
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
    public void setGetTitlesJsonUriTemplate(final String getTitlesJsonUriTemplate) {
        this.getTitlesJsonUriTemplate = getTitlesJsonUriTemplate;
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
