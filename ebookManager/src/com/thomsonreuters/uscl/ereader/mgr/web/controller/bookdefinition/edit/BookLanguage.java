package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BookLanguage {
    ENGLISH("en"),
    FRENCH("fr");

    @Getter
    private final String bookLanguage;
}
