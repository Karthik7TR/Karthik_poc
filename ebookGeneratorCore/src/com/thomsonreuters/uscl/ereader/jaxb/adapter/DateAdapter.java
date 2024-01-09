package com.thomsonreuters.uscl.ereader.jaxb.adapter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {
    @Override
    public Date unmarshal(final String xml) throws Exception {
        return new Date(Long.valueOf(xml));
    }

    @Override
    public String marshal(final Date date) throws Exception {
        return date.getTime() + "";
    }
}
