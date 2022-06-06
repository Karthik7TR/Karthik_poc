package com.thomsonreuters.uscl.ereader.core.book.domain;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType.FILE;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.isEqualsYes;

@Data
@Entity
@Table(name = "COMB_BOOK_DEFN")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CombinedBookDefinition {
    private static final long MOCK_ID_FOR_COMBINED_BOOK = -1L;
    @Column(name = "COMB_BOOK_DEFN_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "CombinedBookDefinitionSequence")
    @SequenceGenerator(name = "CombinedBookDefinitionSequence", sequenceName = "COMB_BOOK_DEFN_ID_SEQ")
    private Long id;

    @OneToMany(mappedBy = "combinedBookDefinition", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<CombinedBookDefinitionSource> sources;

    @Column(name = "IS_DELETED_FLAG", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private String isDeletedFlag;

    public CombinedBookDefinitionSource getPrimaryTitle() {
        return sources.stream()
                .filter(CombinedBookDefinitionSource::isPrimarySource)
                .findFirst()
                .orElseThrow(() -> new EBookException("Primary title was not found"));
    }

    public boolean isBookDefinitionDeletedFlag() {
        log.info("Inside isBookDefinitionDeletedFlag");
        List<BookDefinition> bookDefinitionList = sources.stream()
                .map(CombinedBookDefinitionSource::getBookDefinition)
                .collect(Collectors.toList());
        log.info("Inside isBookDefinitionDeletedFlag bookDefinitionList= " + bookDefinitionList);

        //If for the CombinedBookDefinition, the underlying books are deleted disable Generate button
        if (bookDefinitionList == null || bookDefinitionList.size() == 0) {
            log.info("Inside isBookDefinitionDeletedFlag book null or size =0");
            return true;
        }
        log.info("Inside isBookDefinitionDeletedFlag size of bookDefinitionList= " + bookDefinitionList.size());

        //On delete books: bookDefinitionList= [null, null, null]
        long countOfDeletedBooks = bookDefinitionList.stream()
                .filter(book -> book != null && book.isDeletedFlag())
                .count();
        log.info("Inside isBookDefinitionDeletedFlag countOfDeletedBooks=" + countOfDeletedBooks);
        return countOfDeletedBooks >= 1 ? true : false;
    }

    public void setIsDeletedFlag(final boolean isDeletedFlag) {
        this.isDeletedFlag = getStringForBooleanValue(isDeletedFlag);
    }

    public boolean isDeletedFlag() {
        return isEqualsYes(isDeletedFlag);
    }

    public List<BookDefinition> getOrderedBookDefinitionList() {
        return sources.stream()
                .sorted(Comparator.comparingInt(CombinedBookDefinitionSource::getSequenceNum))
                .map(CombinedBookDefinitionSource::getBookDefinition)
                .collect(Collectors.toList());
    }

    public boolean hasFileSourceType() {
        return sources.stream()
                .map(CombinedBookDefinitionSource::getBookDefinition)
                .map(BookDefinition::getSourceType)
                .anyMatch(item -> FILE == item);
    }

    public boolean allBookDefinitionsExist() {
        return sources.stream()
                .allMatch(source -> source.getBookDefinition() != null);
    }
    public static CombinedBookDefinition fromBookDefinition(final BookDefinition bookDefinition) {
        Set<CombinedBookDefinitionSource> sources = Stream.of(bookDefinition)
                .map(book -> CombinedBookDefinitionSource.builder()
                        .bookDefinition(bookDefinition)
                        .sequenceNum(1)
                        .isPrimarySource(getStringForBooleanValue(true))
                        .build())
                .collect(Collectors.toSet());
        return new CombinedBookDefinition(MOCK_ID_FOR_COMBINED_BOOK, sources, getStringForBooleanValue(false));
    }

    public boolean isFromBookDefinition() {
        return this.getId() == MOCK_ID_FOR_COMBINED_BOOK;
    }
}
