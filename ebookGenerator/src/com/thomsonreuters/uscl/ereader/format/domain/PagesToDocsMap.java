package com.thomsonreuters.uscl.ereader.format.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;

public class PagesToDocsMap {
    @Getter
    private Map<String, DocumentsUuidsWithSectionsForPageNumber> map;

    public PagesToDocsMap() {
        map = new HashMap<>();
    }

    public void addMainPageDocUuid(final String pageLabel, final String docUuid) {
        map.computeIfAbsent(pageLabel, val -> new DocumentsUuidsWithSectionsForPageNumber()).addDocUuidToMainSection(docUuid);
    }

    public void addFootnotePageDocUuid(final String pageLabel, final String docUuid) {
        map.computeIfAbsent(pageLabel, val -> new DocumentsUuidsWithSectionsForPageNumber()).addDocUuidToFootnoteSection(docUuid);
    }

    public void cleanAndPutInOrder() {
        map.entrySet().removeIf(entry -> entry.getValue().isEmptyAfterCleanUp());
        map = new TreeMap<>(map);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void forEach(final BiConsumer<String, DocumentsUuidsWithSectionsForPageNumber> action) {
        map.forEach(action);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DocumentsUuidsWithSectionsForPageNumber {
        @JsonProperty("main-section")
        private Set<String> documentsWithMainSection;
        @JsonProperty("footnote-section")
        private Set<String> documentsWithFootnotesSection;

        private void addDocUuidToMainSection(final String pageNumber) {
            if (documentsWithMainSection == null) {
                documentsWithMainSection = new HashSet<>();
            }
            documentsWithMainSection.add(pageNumber);
        }

        private void addDocUuidToFootnoteSection(final String pageNumber) {
            if (documentsWithFootnotesSection == null) {
                documentsWithFootnotesSection = new HashSet<>();
            }
            documentsWithFootnotesSection.add(pageNumber);
        }

        @JsonIgnore
        public boolean isEmptyAfterCleanUp() {
            cleanUpFromSingleDocumentAndOrder();
            return isEmpty();
        }

        private void cleanUpFromSingleDocumentAndOrder() {
            documentsWithMainSection = cleanUpFromSingleDocumentAndOrder(documentsWithMainSection);
            documentsWithFootnotesSection = cleanUpFromSingleDocumentAndOrder(documentsWithFootnotesSection);
        }

        private Set<String> cleanUpFromSingleDocumentAndOrder(final Set<String> documentsWithMainSection) {
            if (documentsWithMainSection != null) {
                if (documentsWithMainSection.size() == 1) {
                    return null;
                } else {
                    return new TreeSet<>(documentsWithMainSection);
                }
            }
            return null;
        }

        private boolean isEmpty() {
            return documentsWithMainSection == null && documentsWithFootnotesSection == null;
        }
    }
}
