package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.group.service.GroupDefinitionParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is responsible for processing information received from ProView into standard project datatypes.
 *
 * @author uc209819
 *
 */
public class ProviewHandlerImpl implements ProviewHandler
{
    public static final String ROOT_ELEMENT = "group";

    private static final Logger LOG = LogManager.getLogger(ProviewHandlerImpl.class);
    private ProviewClient proviewClient;

    /*----------------------ProView Group--------------------------*/

    @Override
    public Map<String, ProviewGroupContainer> getAllProviewGroupInfo() throws ProviewException
    {
        final String allGroupsResponse = proviewClient.getAllProviewGroups();
        final ProviewGroupsParser parser = new ProviewGroupsParser();
        return parser.process(allGroupsResponse);
    }

    @Override
    public ProviewGroupContainer getProviewGroupContainerById(final String groupId) throws ProviewException
    {
        final String allGroupsResponse = proviewClient.getProviewGroupById(groupId);
        final ProviewGroupsParser parser = new ProviewGroupsParser();
        return parser.process(allGroupsResponse).get(groupId);
    }

    @Override
    public List<GroupDefinition> getGroupDefinitionsById(final String groupId) throws ProviewException
    {
        final String response = proviewClient.getProviewGroupById(groupId);
        final GroupDefinitionParser parser = new GroupDefinitionParser();
        try
        {
            return parser.parse(response);
        }
        catch (final Exception e)
        {
            throw new ProviewException(e.getMessage());
        }
    }

    @Override
    public GroupDefinition getGroupDefinitionByVersion(final String groupId, final long groupVersion)
        throws ProviewException
    {
        final String response =
            proviewClient.getProviewGroupInfo(groupId, GroupDefinition.VERSION_NUMBER_PREFIX + groupVersion);
        final GroupDefinitionParser parser = new GroupDefinitionParser();
        final List<GroupDefinition> groups;
        try
        {
            groups = parser.parse(response);
        }
        catch (final Exception e)
        {
            throw new ProviewException(e.getMessage());
        }
        if (groups.size() == 1)
        {
            return groups.get(0);
        }
        return null;
    }

    @Override
    public List<ProviewGroup> getAllLatestProviewGroupInfo() throws ProviewException
    {
        final List<ProviewGroup> allLatestProviewTitles = new ArrayList<>();
        final Map<String, ProviewGroupContainer> groupMap = getAllProviewGroupInfo();

        for (final String groupId : groupMap.keySet())
        {
            final ProviewGroupContainer groupContainer = groupMap.get(groupId);
            final ProviewGroup latestVersion = groupContainer.getLatestVersion();

            latestVersion.setTotalNumberOfVersions(groupContainer.getProviewGroups().size());

            allLatestProviewTitles.add(latestVersion);
        }
        return allLatestProviewTitles;
    }

    @Override
    public List<ProviewGroup> getAllLatestProviewGroupInfo(final Map<String, ProviewGroupContainer> groupMap)
        throws ProviewException
    {
        final List<ProviewGroup> allLatestProviewGroups = new ArrayList<>();

        for (final String groupId : groupMap.keySet())
        {
            final ProviewGroupContainer groupContainer = groupMap.get(groupId);
            final ProviewGroup latestVersion = groupContainer.getLatestVersion();
            latestVersion.setTotalNumberOfVersions(groupContainer.getProviewGroups().size());
            allLatestProviewGroups.add(latestVersion);
        }

        return allLatestProviewGroups;
    }

    @Override
    public String createGroup(final GroupDefinition groupDefinition)
        throws ProviewException, UnsupportedEncodingException
    {
        return proviewClient.createGroup(
            groupDefinition.getGroupId(),
            groupDefinition.getProviewGroupVersionString(),
            buildRequestBody(groupDefinition));
    }

    @Override
    public String promoteGroup(final String groupId, final String groupVersion) throws ProviewException
    {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewGroup object
        return proviewClient.promoteGroup(groupId, groupVersion);
    }

    @Override
    public String removeGroup(final String groupId, final String groupVersion) throws ProviewException
    {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewGroup object
        return proviewClient.removeGroup(groupId, groupVersion);
    }

    @Override
    public String deleteGroup(final String groupId, final String groupVersion) throws ProviewException
    {
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
    public Map<String, ProviewTitleContainer> getAllProviewTitleInfo() throws ProviewException
    {
        final String allPublishedTitleResponse = proviewClient.getAllPublishedTitles();

        final PublishedTitleParser parser = new PublishedTitleParser();

        return parser.process(allPublishedTitleResponse);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getProviewTitleContainer(java.lang.String)
     */
    @Override
    public ProviewTitleContainer getProviewTitleContainer(final String fullyQualifiedTitleId) throws ProviewException
    {
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
    public List<ProviewTitleInfo> getAllLatestProviewTitleInfo() throws ProviewException
    {
        final Map<String, ProviewTitleContainer> titleMap = getAllProviewTitleInfo();
        return getAllLatestProviewTitleInfo(titleMap);
    }

    @Override
    public List<ProviewTitleInfo> getAllLatestProviewTitleInfo(final Map<String, ProviewTitleContainer> titleMap)
        throws ProviewException
    {
        final List<ProviewTitleInfo> allLatestProviewTitles = new ArrayList<>();

        for (final String bookId : titleMap.keySet())
        {
            final ProviewTitleContainer titleContainer = titleMap.get(bookId);
            final ProviewTitleInfo latestVersion = titleContainer.getLatestVersion();
            latestVersion.setTotalNumberOfVersions(titleContainer.getProviewTitleInfos().size());
            allLatestProviewTitles.add(latestVersion);
        }

        return allLatestProviewTitles;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient# getCurrentProviewTitleInfo(java.lang.String)
     */
    @Override
    public ProviewTitleInfo getLatestProviewTitleInfo(final String fullyQualifiedTitleId) throws ProviewException
    {
        ProviewTitleInfo latestProviewVersion = null;
        try
        {
            final ProviewTitleContainer proviewTitleContainer = getProviewTitleContainer(fullyQualifiedTitleId);

            if (proviewTitleContainer != null)
            {
                latestProviewVersion = proviewTitleContainer.getLatestVersion();
            }
        }
        catch (final ProviewException ex)
        {
            final String errorMessage = ex.getMessage();
            if (!errorMessage.contains("does not exist"))
            {
                throw ex;
            }
        }
        return latestProviewVersion;
    }
    /*
    @Override
    public ProviewTitleInfo getProviewTitleInfoByVersion(String fullyQualifiedTitleId, String version) throws ProviewException {
    	ProviewTitleContainer proviewTitleContainer = null;
    	String publishedTitleResponse = proviewClient.getSingleTitleInfoByVersion(fullyQualifiedTitleId, version);

    	PublishedTitleParser parser = new PublishedTitleParser();
    	Map<String, ProviewTitleContainer> titleMap = parser.process(publishedTitleResponse);

    	proviewTitleContainer = titleMap.get(fullyQualifiedTitleId);
    	// TODO: verify ProviewClient.getSingleTitleInfoByVersion() returns xml in the same
    	// format as ProviewClient.getSinglePublishedTitle()
    	return proviewTitleContainer.getProviewTitleInfos().get(0);
    }
    */

    @Override
    public List<GroupDetails> getSingleTitleGroupDetails(final String fullyQualifiedTitleId) throws ProviewException
    {
        List<GroupDetails> proviewGroupDetails = new ArrayList<>();
        try
        {
            final String response = proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
            final ProviewSingleTitleParser singleTitleParser = new ProviewSingleTitleParser();
            proviewGroupDetails = singleTitleParser.process(response);
        }
        catch (final Exception e)
        {
            throw new ProviewException(e.getMessage());
        }
        return proviewGroupDetails;
    }

    @Override
    public boolean hasTitleIdBeenPublished(final String fullyQualifiedTitleId) throws ProviewException
    {
        final ProviewTitleContainer proviewTitleContainer = getProviewTitleContainer(fullyQualifiedTitleId);

        if (proviewTitleContainer != null)
        {
            return proviewTitleContainer.hasBeenPublished();
        }

        return false;
    }

    @Override
    public boolean isTitleInProview(final String fullyQualifiedTitleId) throws ProviewException
    {
        try
        {
            proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
            return true;
        }
        catch (final Exception ex)
        {
            final String errorMessage = ex.getMessage();
            if (errorMessage.contains("does not exist"))
            {
                return false;
            }
            else
            {
                throw ex;
            }
        }
    }

    @Override
    public String publishTitle(final String fullyQualifiedTitleId, final String versionNumber, final File eBook) throws ProviewException
    {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        return proviewClient.publishTitle(fullyQualifiedTitleId, versionNumber, eBook);
    }

    @Override
    public String promoteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException
    {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        return proviewClient.promoteTitle(fullyQualifiedTitleId, eBookVersionNumber);
    }

    @Override
    public String removeTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException
    {
        // TODO Change return to boolean (success) and move validation from calling classes to this method
        // TODO Change input type to single ProviewTitle object
        return proviewClient.removeTitle(fullyQualifiedTitleId, eBookVersionNumber);
    }

    @Override
    public boolean deleteTitle(final String fullyQualifiedTitleId, final String eBookVersionNumber) throws ProviewException
    {
        // TODO Change input type to single ProviewTitle object, move validation from calling classes to this method
        try
        {
            proviewClient.deleteTitle(fullyQualifiedTitleId, eBookVersionNumber);
        }
        catch (final ProviewRuntimeException ex)
        {
            final String errorMsg = ex.getMessage();
            if (ex.getStatusCode().equals("400") && errorMsg.contains("Title already exists in publishing queue"))
            {
                return false;
            }
        }
        return true;
    }

    public String buildRequestBody(final GroupDefinition groupDefinition) throws UnsupportedEncodingException
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        try
        {
            final XMLStreamWriter writer = outputFactory.createXMLStreamWriter(output, "UTF-8");

            writer.writeStartElement(ROOT_ELEMENT);
            writer.writeAttribute("id", groupDefinition.getGroupId());

            writeElement(writer, "name", groupDefinition.getName());
            writeElement(writer, "type", groupDefinition.getType());
            writeElement(writer, "headtitle", groupDefinition.getHeadTitle());
            writer.writeStartElement("members");
            for (final SubGroupInfo subGroupInfo : groupDefinition.getSubGroupInfoList())
            {
                writer.writeStartElement("subgroup");
                if (subGroupInfo.getHeading() != null && subGroupInfo.getHeading().length() > 0)
                {
                    writer.writeAttribute("heading", subGroupInfo.getHeading());
                }
                for (final String title : subGroupInfo.getTitles())
                {
                    writeElement(writer, "title", title);
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.close();
        }
        catch (final XMLStreamException e)
        {
            throw new RuntimeException(e.toString(), e);
        }

        LOG.debug("Proview[ Request: " + output.toString() + "]");
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
    protected void writeElement(final XMLStreamWriter writer, final String name, final Object value) throws XMLStreamException
    {
        if (value != null)
        {
            writer.writeStartElement(name);
            writer.writeCharacters(value.toString().trim());
            writer.writeEndElement();
        }
    }

    @Required
    public void setProviewClient(final ProviewClient proviewClient)
    {
        this.proviewClient = proviewClient;
    }
}
