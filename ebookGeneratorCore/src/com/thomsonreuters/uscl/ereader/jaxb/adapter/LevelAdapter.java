package com.thomsonreuters.uscl.ereader.jaxb.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Level;

public class LevelAdapter extends XmlAdapter<String, Level> {
    @Override
    public Level unmarshal(final String xml) throws Exception {
        return Level.toLevel(xml);
    }

    @Override
    public String marshal(final Level level) throws Exception {
        return level.toString();
    }
}
