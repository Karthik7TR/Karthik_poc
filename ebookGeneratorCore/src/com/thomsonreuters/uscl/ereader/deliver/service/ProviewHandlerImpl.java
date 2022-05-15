package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ExpectedProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.group.service.GroupDefinitionParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is responsible for processing information received from ProView into standard project datatypes.
 *
 * @author uc209819
 *
 */
@Slf4j
public class ProviewHandlerImpl implements ProviewHandler {
    public static final String ROOT_ELEMENT = "group";

    private ProviewClient proviewClient;
    @Autowired
    private SupersededProviewHandlerHelper supersededHandler;
    @Autowired
    private SplitPartsUniteService splitPartsUniteService;
    @Autowired
    private PublisherCodeService publisherCodeService;

    /*----------------------ProView Group--------------------------*/

    @Override
    public Map<String, ProviewGroupContainer> getAllProviewGroupInfo() throws ProviewException {
        Map<String, ProviewGroupContainer> allGroups = new HashMap<>();
        List<PublisherCode> publishers = publisherCodeService.getAllPublisherCodes();
        final ProviewGroupsParser parser = new ProviewGroupsParser();
        for (PublisherCode publisher : publishers) {
            final String groupsResponse = proviewClient.getAllProviewGroups(publisher.getName());
            allGroups.putAll(parser.process(groupsResponse));
        }
        return allGroups;
    }

    @Override
    public ProviewGroupContainer getProviewGroupContainerById(final String groupId) throws ProviewException {
        final String allGroupsResponse = proviewClient.getProviewGroupById(groupId);
        final ProviewGroupsParser parser = new ProviewGroupsParser();
        return parser.process(allGroupsResponse).get(groupId);
    }

    @Override
    public List<GroupDefinition> getGroupDefinitionsById(final String groupId) throws ProviewException {
        final String response = proviewClient.getProviewGroupById(groupId);
        final GroupDefinitionParser parser = new GroupDefinitionParser();
        try {
            return parser.parse(response);
        } catch (final Exception e) {
            throw new ProviewException(e.getMessage(), e);
        }
    }

    @Override
    public GroupDefinition getGroupDefinitionByVersion(final String groupId, final long groupVersion)
        throws ProviewException {
        final String response =
            proviewClient.getProviewGroupInfo(groupId, GroupDefinition.VERSION_NUMBER_PREFIX + groupVersion);
        final GroupDefinitionParser parser = new GroupDefinitionParser();
        final List<GroupDefinition> groups;
        try {
            groups = parser.parse(response);
        } catch (final Exception e) {
            throw new ProviewException(e.getMessage(), e);
        }
        if (groups.size() == 1) {
            return groups.get(0);
        }
        return null;
    }

    @Override
    public List<ProviewGroup> getAllLatestProviewGroupInfo() throws ProviewException {
        final List<ProviewGroup> allLatestProviewTitles = new ArrayList<>();
        final Map<String, ProviewGroupContainer> groupMap = getAllProviewGroupInfo();

        for (final String groupId : groupMap.keySet()) {
            final ProviewGroupContainer groupContainer = groupMap.get(groupId);
            final ProviewGroup latestVersion = groupContainer.getLatestVersion();

            latestVersion.setTotalNumberOfVersions(groupContainer.getProviewGroups().size());

            allLatestProviewTitles.add(latestVersion);
        }
        return allLatestProviewTitles;
    }

    @Override
    public List<ProviewGroup> getAllLatestProviewGroupInfo(final Map<String, ProviewGroupContainer> groupMap) {
        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();

        for (final String groupId : groupMap.keySet()) {
            final ProviewGroupContainer groupContainer = groupMap.get(groupId);
            final ProviewGroup latestVersion = groupContainer.getLatestVersion();
            latestVersion.setTotalNumberOfVersions(groupContainer.getProviewGroups().size());
            allLatestProviewGroups.add(latestVersion);
        }

        return allLatestProviewGroups;
    }

    @Override
    public String createGroup(final GroupDefinition groupDefinition)
        throws ProviewException, UnsupportedEncodingException {
        return proviewClient.createGroup(
            groupDefinition.getGroupId(),
            groupDefinition.getProviewGroupVersionString(),
            buildRequestBody(groupDefinition));
    }

    @Override
    public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewGroup object
        return proviewClient.promoteGroup(groupId, groupVersion);
    }

    @Override
    public String removeGroup(final String groupId, final String groupVersion) throws ProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewGroup object
        return proviewClient.removeGroup(groupId, groupVersion);
    }

    @Override
    public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewGroup object
        return proviewClient.deleteGroup(groupId, groupVersion);
    }

    /*-----------------------ProView Title-----------------------------*/

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getAllProviewTitleInfo()
     */
    @Override
    public Map<String, ProviewTitleContainer> getAllProviewTitleInfo() throws ProviewException {
        Map<String, ProviewTitleContainer> allTitles = new HashMap<>();
        final PublishedTitleParser parser = new PublishedTitleParser();
        List<PublisherCode> publishers = publisherCodeService.getAllPublisherCodes();
        for (PublisherCode publisher : publishers) {
            final String publishedTitleResponse = proviewClient.getAllPublishedTitles(publisher.getName());
            allTitles.putAll(parser.process(publishedTitleResponse));
        }
        return allTitles;
    }

    @Override
    public List<ProviewTitleReportInfo> getAllProviewTitleReportInfo() throws ProviewException {
        List<ProviewTitleReportInfo> lstProviewTitles = new ArrayList<ProviewTitleReportInfo>();
        List<PublisherCode> publishers = publisherCodeService.getAllPublisherCodes();

        for (PublisherCode publisher : publishers) {
            List<ProviewTitleReportInfo>  lstPublisherTitle = proviewClient.getAllPublishedTitlesJson(publisher.getName());
            List<ProviewTitleReportInfo>  lstPublisherTitleBackup = new CopyOnWriteArrayList<>(lstPublisherTitle);

            //Update totalNumberOfVersions for each title
            for (ProviewTitleReportInfo report : lstPublisherTitle) {
                 long countOfVersionsForTitle =  lstPublisherTitleBackup
                                .stream()
                                .filter(title -> title.getId().equals(report.getId()))
                                .count();
                report.setTotalNumberOfVersions((int) countOfVersionsForTitle);
            };

            lstProviewTitles.addAll(lstPublisherTitle);
            lstPublisherTitleBackup = new CopyOnWriteArrayList<>();
        }
        return lstProviewTitles;
    }


    @Override
    public Map<String, ProviewTitleContainer> getTitlesWithUnitedParts() throws ProviewException {
        Map<String, ProviewTitleContainer> allProviewTitleInfo = getAllProviewTitleInfo();
        return splitPartsUniteService.getTitlesWithUnitedParts(allProviewTitleInfo);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getProviewTitleContainer(java.lang.String)
     */
    @Override
    public ProviewTitleContainer getProviewTitleContainer(final String fullyQualifiedTitleId) throws ProviewException {
        ProviewTitleContainer proviewTitleContainer = null;
        final String publishedTitleResponse = proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);

        final PublishedTitleParser parser = new PublishedTitleParser();
        final Map<String, ProviewTitleContainer> titleMap = parser.process(publishedTitleResponse);

        proviewTitleContainer = titleMap.get(fullyQualifiedTitleId);

        return proviewTitleContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getAllLatestProviewTitleInfo()
     */
    @Override
    public List<ProviewTitleInfo> getAllLatestProviewTitleInfo() throws ProviewException {
        final Map<String, ProviewTitleContainer> titleMap = getAllProviewTitleInfo();
        return getAllLatestProviewTitleInfo(titleMap);
    }

    @Override
    public List<ProviewTitleInfo> getAllLatestProviewTitleInfo(final Map<String, ProviewTitleContainer> titleMap) {
        return titleMap.values().stream()
                .map(titleContainer -> {
                    ProviewTitleInfo latestVersion = titleContainer.getLatestVersion();
                    latestVersion.setTotalNumberOfVersions(titleContainer.getProviewTitleInfos().size());
                    return latestVersion;
                })
                .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getCurrentProviewTitleInfo(java.lang.String)
     */
    @Override
    public ProviewTitleInfo getLatestPublishedProviewTitleInfo(final String fullyQualifiedTitleId) throws ProviewException {
        ProviewTitleInfo latestProviewVersion = null;
        try {
            final ProviewTitleContainer proviewTitleContainer = getProviewTitleContainer(fullyQualifiedTitleId);

            if (proviewTitleContainer != null) {
                latestProviewVersion = proviewTitleContainer.getLatestVersion();
            }
        } catch (final ProviewException ex) {
            final String errorMessage = ex.getMessage();
            if (!errorMessage.contains("does not exist")) {
                throw ex;
            }
        }
        return latestProviewVersion;
    }

    @Override
    public ProviewTitleInfo getLatestProviewTitleInfo(final String fullyQualifiedTitleId) throws ProviewException {
        final ProviewTitleContainer titleContainer = getAllProviewTitleInfo().get(fullyQualifiedTitleId);
        return Optional.ofNullable(titleContainer)
                .map(ProviewTitleContainer::getLatestVersion)
                .orElse(null);
    }

    @Override
    public String getTitleIdCaseSensitiveForVersion(final String fullyQualifiedTitleId, final String version) {
        try {
            String rawTitleInfo = proviewClient.getTitleInfo(fullyQualifiedTitleId, version);
            return new TitleInfoParser(rawTitleInfo).getId();
        } catch (ProviewException e) {
            throw new EBookException(e);
        }
    }

    @Override
    public List<GroupDetails> getSingleTitleGroupDetails(final String fullyQualifiedTitleId) throws ProviewException {
        List<GroupDetails> proviewGroupDetails;
        try {
            final String response = proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
            final ProviewSingleTitleParser singleTitleParser = new ProviewSingleTitleParser();
            proviewGroupDetails = singleTitleParser.process(response);
        } catch (final Exception e) {
            throw new ProviewException(e.getMessage(), e);
        }
        return proviewGroupDetails;
    }

    @Override
    public boolean hasTitleIdBeenPublished(final String fullyQualifiedTitleId) throws ProviewException {
        final ProviewTitleContainer proviewTitleContainer = getProviewTitleContainer(fullyQualifiedTitleId);

        if (proviewTitleContainer != null) {
            return proviewTitleContainer.hasBeenPublished();
        }

        return false;
    }

    @Override
    public boolean isTitleInProview(final String fullyQualifiedTitleId) throws ProviewException {
        try {
            proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
            return true;
        } catch (final Exception ex) {
            final String errorMessage = ex.getMessage();
            if (errorMessage.contains("does not exist")) {
                return false;
            } else {
                throw ex;
            }
        }
    }

    @Override
    public String publishTitle(final String fullyQualifiedTitleId, final Version version, final File eBook)
        throws ProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        return proviewClient.publishTitle(fullyQualifiedTitleId, version.getFullVersion(), eBook);
    }

    @Override
    public boolean promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber)
        throws ProviewException, ExpectedProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        final boolean isSuccessful = proviewClient.promoteTitle(fullyQualifiedTitleId, eBookVersionNumber).is2xxSuccessful();
        if (new TitleId(fullyQualifiedTitleId).isHeadTitle() && isSuccessful) {
            supersededHandler.markTitleVersionAsSupersededInThread(fullyQualifiedTitleId, new Version(eBookVersionNumber), getAllProviewTitleInfo());
        }

        return isSuccessful;
    }

    @Override
    public void markTitleSuperseded(final String fullyQualifiedTitleId) throws ProviewException {
        supersededHandler.markTitleSuperseded(fullyQualifiedTitleId, getAllProviewTitleInfo());
    }

    @Override
    public boolean removeTitle(final String fullyQualifiedTitleId, final Version version) throws ProviewException {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        return proviewClient.removeTitle(fullyQualifiedTitleId, version.getFullVersion()).is2xxSuccessful();
    }

    @Override
    public boolean deleteTitle(final String fullyQualifiedTitleId, final Version version) throws ProviewException {
        // TODO Change input type to single ProviewTitle object, move validation from calling classes to this method
        return proviewClient.deleteTitle(fullyQualifiedTitleId, version.getFullVersion()).is2xxSuccessful();
    }

    public String buildRequestBody(final GroupDefinition groupDefinition) throws UnsupportedEncodingException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        try {
            final XMLStreamWriter writer = outputFactory.createXMLStreamWriter(output, "UTF-8");

            writer.writeStartElement(ROOT_ELEMENT);
            writer.writeAttribute("id", groupDefinition.getGroupId());

            writeElement(writer, "name", groupDefinition.getName());
            writeElement(writer, "type", groupDefinition.getType());
            writeElement(writer, "headtitle", groupDefinition.getHeadTitle());
            writer.writeStartElement("members");
            for (final SubGroupInfo subGroupInfo : groupDefinition.getSubGroupInfoList()) {
                writer.writeStartElement("subgroup");
                if (subGroupInfo.getHeading() != null && subGroupInfo.getHeading().length() > 0) {
                    writer.writeAttribute("heading", subGroupInfo.getHeading());
                }
                for (final String title : subGroupInfo.getTitles()) {
                    writeElement(writer, "title", title);
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.close();
        } catch (final XMLStreamException e) {
            throw new RuntimeException(e.toString(), e);
        }

        log.debug("Proview[ Request: " + output.toString() + "]");
        return output.toString(StandardCharsets.UTF_8.displayName());
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

    @Required
    public void setProviewClient(final ProviewClient proviewClient) {
        this.proviewClient = proviewClient;
    }
}
