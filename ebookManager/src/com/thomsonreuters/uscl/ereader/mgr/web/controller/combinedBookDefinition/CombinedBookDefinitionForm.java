package com.thomsonreuters.uscl.ereader.mgr.web.controller.combinedBookDefinition;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.text.StringEscapeUtils.escapeXml10;

public class CombinedBookDefinitionForm {
    public static final String FORM_NAME = "combinedBookDefinitionForm";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Getter
    @Setter
    private Long id;

    private Set<CombinedBookDefinitionSource> sources = new HashSet<>();

    @Getter
    @Setter
    private boolean isDeletedFlag = false;

    public String getSources() throws JsonProcessingException {
        return escapeXml10(OBJECT_MAPPER.writeValueAsString(sources));
    }

    public void setSources(final String sources) throws IOException {
        this.sources = OBJECT_MAPPER.readValue(
                sources,
                OBJECT_MAPPER.getTypeFactory().constructCollectionType(Set.class, CombinedBookDefinitionSource.class));
    }

    public Set<CombinedBookDefinitionSource> getSourcesSet() {
        return sources;
    }

    public void setSourcesSet(final Set<CombinedBookDefinitionSource> sources) {
        this.sources = sources;
    }

    public void loadCombinedBookDefinition(final CombinedBookDefinition book) {
        this.sources.forEach(item -> item.setCombinedBookDefinition(book));
        book.setSources(this.sources);
        book.setIsDeletedFlag(this.isDeletedFlag);
        book.setId(this.id);
    }

    public void initialize(final CombinedBookDefinition combinedBookDefinition) {
        this.id = combinedBookDefinition.getId();
        this.isDeletedFlag = combinedBookDefinition.isDeletedFlag();
        this.sources = combinedBookDefinition.getSources().stream()
                .map(source -> {
                    Optional.ofNullable(source.getBookDefinition())
                            .ifPresent(book -> {
                                BookDefinition bookDefinition = new BookDefinition();
                                bookDefinition.setFullyQualifiedTitleId(source.getBookDefinition().getFullyQualifiedTitleId());
                                bookDefinition.setSourceType(source.getBookDefinition().getSourceType());
                                bookDefinition.setEbookDefinitionId(source.getBookDefinition().getEbookDefinitionId());
                                source.setBookDefinition(bookDefinition);
                            });
                    return source;
                }).collect(Collectors.toSet());
    }
}
