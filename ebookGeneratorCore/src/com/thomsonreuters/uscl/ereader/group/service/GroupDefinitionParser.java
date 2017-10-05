package com.thomsonreuters.uscl.ereader.group.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition.SubGroupInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GroupDefinitionParser {
    private static final String GROUP = "group";
    private static final String GROUP_NAME = "name";
    private static final String GROUP_TYPE = "type";
    private static final String GROUP_HEAD_TITLE = "headtitle";
    private static final String GROUP_ID = "id";
    private static final String GROUP_STATUS = "status";
    private static final String GROUP_VERSION = "version";

    private static final String SUBGROUP = "subgroup";
    private static final String SUBGROUP_TITLE = "title";
    private static final String SUBGROUP_HEADING = "heading";

    private XMLInputFactory factory;

    private static Logger LOG = LogManager.getLogger(GroupDefinitionParser.class);

    public GroupDefinitionParser() {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
    }

    public List<GroupDefinition> parse(final String text) throws Exception {
        StringBuffer buffer = new StringBuffer();
        XMLEventReader r = null;

        final List<GroupDefinition> groupDefinitions = new ArrayList<>();
        GroupDefinition groupDefinition = null;
        SubGroupInfo subGroupInfo = null;
        try (InputStream input = new ByteArrayInputStream(text.getBytes("UTF-8"));) {
            r = factory.createXMLEventReader(input, "UTF-8");

            while (r.hasNext()) {
                final XMLEvent event = r.nextEvent();

                if (event.isStartElement()) {
                    final StartElement element = event.asStartElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(GROUP)) {
                        groupDefinition = new GroupDefinition();
                        final Attribute idAttr = element.asStartElement().getAttributeByName(new QName(GROUP_ID));
                        final Attribute statusAttr =
                            element.asStartElement().getAttributeByName(new QName(GROUP_STATUS));
                        final Attribute versionAttr =
                            element.asStartElement().getAttributeByName(new QName(GROUP_VERSION));

                        if (idAttr != null) {
                            groupDefinition.setGroupId(idAttr.getValue());
                        }
                        if (statusAttr != null) {
                            groupDefinition.setStatus(statusAttr.getValue());
                        }
                        if (versionAttr != null) {
                            groupDefinition.setProviewGroupVersionString(versionAttr.getValue());
                        }
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP)) {
                        subGroupInfo = new SubGroupInfo();
                        final Attribute subheadingAttr =
                            element.asStartElement().getAttributeByName(new QName(SUBGROUP_HEADING));
                        if (subheadingAttr != null) {
                            final String subheading = subheadingAttr.getValue();
                            if (StringUtils.isNotBlank(subheading)) {
                                subGroupInfo.setHeading(subheading);
                            }
                        }
                    }
                    buffer = new StringBuffer();
                }

                if (event.isCharacters()) {
                    final Characters character = event.asCharacters();
                    buffer.append(character.getData());
                }

                if (event.isEndElement() && groupDefinition != null) {
                    final EndElement element = event.asEndElement();
                    if (element.getName().getLocalPart().equalsIgnoreCase(GROUP)) {
                        groupDefinitions.add(groupDefinition);
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(GROUP_NAME)) {
                        groupDefinition.setName(buffer.toString());
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(GROUP_HEAD_TITLE)) {
                        groupDefinition.setHeadTitle(buffer.toString());
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(GROUP_TYPE)) {
                        groupDefinition.setType(buffer.toString());
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP_TITLE)
                        && subGroupInfo != null) {
                        subGroupInfo.addTitle(buffer.toString());
                    } else if (element.getName().getLocalPart().equalsIgnoreCase(SUBGROUP)) {
                        groupDefinition.addSubGroupInfo(subGroupInfo);
                    }
                }
            }
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return groupDefinitions;
    }
}
