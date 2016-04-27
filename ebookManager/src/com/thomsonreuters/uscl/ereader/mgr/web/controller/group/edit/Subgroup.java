package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.List;

import org.springframework.util.AutoPopulatingList;

public class Subgroup {
	private String heading;
	private List<Title> titles = new AutoPopulatingList<Title>(Title.class);
	

	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public List<Title> getTitles() {
		return titles;
	}
	
	public void addTitle(Title title) {
		this.titles.add(title);
	}
	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}
}
