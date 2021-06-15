package com.thomsonreuters.uscl.ereader.core.book.domain;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import lombok.Data;
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
import java.util.Set;

import static com.thomsonreuters.uscl.ereader.util.ValueConverter.getStringForBooleanValue;
import static com.thomsonreuters.uscl.ereader.util.ValueConverter.isEqualsYes;

@Data
@Entity
@Table(name = "COMB_BOOK_DEFN")
public class CombinedBookDefinition {
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

    public void setIsDeletedFlag(final boolean isDeletedFlag) {
        this.isDeletedFlag = getStringForBooleanValue(isDeletedFlag);
    }

    public boolean isDeletedFlag() {
        return isEqualsYes(isDeletedFlag);
    }
}
