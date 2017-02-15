package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.SubgroupInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse all Group info from proview
 * @author uc209819
 *
 */
public class ProviewGroupsParser
{
    private Map<String, ProviewGroupContainer> groupMap = new HashMap<String, ProviewGroupContainer>();

    /**
     *
     * @param xml
     *            all title info from proview
     * @return Generate a map of the ProviewGroupContainer objects where the key
     *         is the group id.
     */
    public Map<String, ProviewGroupContainer> process(final String xml)
    {
        final Logger LOG = LogManager.getLogger(ProviewGroupsParser.class);

        try
        {
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setNamespaceAware(true);

            final XMLReader reader = parserFactory.newSAXParser().getXMLReader();
            reader.setContentHandler(new DefaultHandler()
            {
                private static final String GROUP = "group";
                private static final String SUBGROUP = "subgroup";
                private static final String HEADTITLE = "headtitle";
                private static final String NAME = "name";
                private static final String TITLE = "title";
                private static final String ID = "id";
                private static final String VERSION = "version";
                private static final String STATUS = "status";

                private StringBuffer charBuffer;
                private ProviewGroup proviewGroup;
                private SubgroupInfo subgroup;

                /*
                 * (non-Javadoc)
                 *
                 * @see org.xml.sax.helpers.DefaultHandler#characters(char[],
                 * int, int)
                 */
                @Override
                public void characters(final char[] ch, final int start, final int length) throws SAXException
                {
                    if (charBuffer != null)
                    {
                        charBuffer.append(new String(ch, start, length));
                    }
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String
                 * , java.lang.String, java.lang.String)
                 */
                @Override
                public void endElement(final String uri, final String localName, final String qName) throws SAXException
                {
                    try
                    {
                        String value = null;

                        if (charBuffer != null)
                        {
                            value = StringUtils.trim(charBuffer.toString());
                        }
                        if (NAME.equals(qName))
                        {
                            proviewGroup.setGroupName(value);
                        }
                        else if (HEADTITLE.equals(qName))
                        {
                            proviewGroup.setHeadTitle(StringUtils.substringBeforeLast(value, "/v"));
                        }
                        else if (GROUP.equals(qName))
                        {
                            if (groupMap.get(proviewGroup.getGroupId()) == null)
                            {
                                groupMap.put(proviewGroup.getGroupId(), new ProviewGroupContainer());
                            }
                            groupMap.get(proviewGroup.getGroupId()).getProviewGroups().add(proviewGroup);
                        }
                        else if (TITLE.equals(qName))
                        {
                            subgroup.getTitleIdList().add(value);
                        }

                        charBuffer = null;
                    }
                    catch (final Exception e)
                    {
                        final String message =
                            "PublishedTitleParser: Exception occured during PublishedTitleParser parsing endElement. The error message is: "
                                + e.getMessage();
                        LOG.error(message, e);
                        throw new RuntimeException(message, e);
                    }
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang
                 * .String, java.lang.String, java.lang.String,
                 * org.xml.sax.Attributes)
                 */
                @Override
                public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
                    throws SAXException
                {
                    try
                    {
                        charBuffer = new StringBuffer();
                        if (GROUP.equals(qName))
                        {
                            proviewGroup = new ProviewGroup();
                            proviewGroup.setSubgroupInfoList(new ArrayList<SubgroupInfo>());
                            proviewGroup.setGroupId(atts.getValue(ID));
                            proviewGroup.setGroupStatus(atts.getValue(STATUS));
                            proviewGroup.setGroupVersion(atts.getValue(VERSION));
                            proviewGroup.setGroupIdByVersion(atts.getValue(ID) + "/" + atts.getValue(VERSION));
                        }
                        else if (SUBGROUP.equals(qName))
                        {
                            subgroup = new SubgroupInfo();
                            subgroup.setSubGroupName(atts.getValue("heading"));
                            subgroup.setTitleIdList(new ArrayList<String>());
                            proviewGroup.getSubgroupInfoList().add(subgroup);
                        }
                        else if (NAME.equals(qName))
                        {
                            charBuffer = new StringBuffer();
                        }
                    }
                    catch (final Exception e)
                    {
                        final String message =
                            "PublishedTitleParser: Exception  PublishedTitleParser parsing startElement. The error message is: "
                                + e.getMessage();
                        LOG.error(message, e);
                        throw new RuntimeException(message, e);
                    }
                }
            });
            reader.parse(new InputSource(new StringReader(xml)));
            return groupMap;
        }
        catch (final SAXException e)
        {
            throw new RuntimeException(e);
        }
        catch (final ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
