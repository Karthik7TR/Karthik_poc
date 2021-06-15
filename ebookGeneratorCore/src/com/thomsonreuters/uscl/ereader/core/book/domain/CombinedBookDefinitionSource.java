package com.thomsonreuters.uscl.ereader.core.book.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.isEqualsYes;

@Data
@ToString(exclude = "combinedBookDefinition")
@EqualsAndHashCode(exclude = "combinedBookDefinition")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "COMB_BOOK_DEFN_SOURCE")
public class CombinedBookDefinitionSource {
    @Column(name = "COMB_BOOK_DEFN_SOURCE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "CombinedBookDefinitionSourceSequence")
    @SequenceGenerator(name = "CombinedBookDefinitionSourceSequence", sequenceName = "COMB_BOOK_DEFN_SOURCE_ID_SEQ")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "COMB_BOOK_DEFN_ID", referencedColumnName = "COMB_BOOK_DEFN_ID", nullable = false)})
    private CombinedBookDefinition combinedBookDefinition;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EBOOK_DEFINITION_ID")
    private BookDefinition bookDefinition;

    @Column(name = "IS_PRIMARY_SOURCE", nullable = false)
    private String isPrimarySource;

    @Column(name = "SEQUENCE_NUM", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private Integer sequenceNum;

    public void setIsPrimarySource(final boolean isPrimarySource) {
        this.isPrimarySource = getStringForBooleanValue(isPrimarySource);
    }

    public boolean isPrimarySource() {
        return isEqualsYes(isPrimarySource);
    }
}
