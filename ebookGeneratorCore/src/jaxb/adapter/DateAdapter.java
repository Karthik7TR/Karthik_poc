package jaxb.adapter;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class DateAdapter extends XmlAdapter<String, Date> {

	@Override
	public Date unmarshal(String xml) throws Exception {
		return new Date(Long.valueOf(xml));
	}

	@Override
	public String marshal(Date date) throws Exception {
		return date.getTime() + "";
	}

}