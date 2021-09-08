package com.thomsonreuters.uscl.ereader.core.book.model;

import lombok.Data;

@Data
public class TitleIdAndProviewName {
    private final TitleId titleId;
    private final String proviewName;

    public TitleIdAndProviewName(String titleId, String proviewName) {
        this.titleId = new TitleId(titleId);
        this.proviewName = proviewName;
    }
}
