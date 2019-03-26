package com.thomsonreuters.uscl.ereader.mgr.library.vdo;

import static java.util.Optional.ofNullable;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The filter criteria used when searching for book definitions to display in the Library List table.
 * A null or blank property value indicates that it is to be ignored and not included as part of the search criteria.
 */
@Getter
@NoArgsConstructor
public class LibraryListFilter {
    // Book Definition properties
    private Date from; // start date on and after this calendar date (inclusive)
    private Date to; // start date on and before this calendar date (inclusive)
    private String titleId;
    private String proviewDisplayName;
    private String sourceType;
    private String action;
    private String isbn;
    private String materialId;
    private Long keywordValue;

    public LibraryListFilter(
        final Date from,
        final Date to,
        final String action,
        final String titleId,
        final String proviewDisplayName,
        final String sourceType,
        final String isbn,
        final String materialId,
        final Long keywordValue) {
        this.from = from;
        this.to = to;
        this.action = action;
        this.titleId = ofNullable(titleId).map(this::clearWildcard).map(String::trim).orElse(null);
        this.proviewDisplayName =
            ofNullable(proviewDisplayName).map(this::clearWildcard).map(String::trim).orElse(null);
        this.sourceType = sourceType;
        this.isbn = ofNullable(isbn).map(this::clearWildcard).map(String::trim).orElse(null);
        this.materialId = ofNullable(materialId).map(this::clearWildcard).map(String::trim).orElse(null);
        this.keywordValue = keywordValue;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    private String clearWildcard(final String string) {
        return (EMPTY.equals(string.replaceAll("%", EMPTY))) ? EMPTY : string;
    }
}
