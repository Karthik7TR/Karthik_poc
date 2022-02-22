package com.thomsonreuters.uscl.ereader.format.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DocsToPagesMap {
    @Getter
    private final Map<String, PageNumbersInSectionsForDocument> map;

    public DocsToPagesMap() {
        map = new TreeMap<>();
    }

    public void addDocUuidMainPage(final String docUuid, final String pageLabel) {
        map.computeIfAbsent(docUuid, val -> new PageNumbersInSectionsForDocument()).addPageNumberToMainSection(pageLabel);
    }

    public void addDocUuidFootnotePage(final String docUuid, final String pageLabel) {
        map.computeIfAbsent(docUuid, val -> new PageNumbersInSectionsForDocument()).addPageNumberToFootnoteSection(pageLabel);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageNumbersInSectionsForDocument {
        @JsonProperty("main-section")
        private Set<String> pageNumbersInMainSection;
        @JsonProperty("footnote-section")
        private Set<String> pageNumbersInFootnotesSection;

        private void addPageNumberToMainSection(final String pageNumber) {
            if (pageNumbersInMainSection == null) {
                pageNumbersInMainSection = new TreeSet<>();
            }
            pageNumbersInMainSection.add(pageNumber);
        }

        private void addPageNumberToFootnoteSection(final String pageNumber) {
            if (pageNumbersInFootnotesSection == null) {
                pageNumbersInFootnotesSection = new TreeSet<>();
            }
            pageNumbersInFootnotesSection.add(pageNumber);
        }
    }
}
