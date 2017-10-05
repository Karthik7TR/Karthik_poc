package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.edit;

import java.util.List;

import org.springframework.util.AutoPopulatingList;

public class Subgroup {
    private String heading;
    private List<Title> titles = new AutoPopulatingList<>(Title.class);

    public String getHeading() {
        return heading;
    }

    public void setHeading(final String heading) {
        this.heading = heading;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void addTitle(final Title title) {
        titles.add(title);
    }

    public void setTitles(final List<Title> titles) {
        this.titles = titles;
    }
}
