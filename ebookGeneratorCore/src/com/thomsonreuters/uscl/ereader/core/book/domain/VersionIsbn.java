package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.thomsonreuters.uscl.ereader.core.book.domain.common.EbookDefinitionAware;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Entity
@EqualsAndHashCode(of = {"id"})
@RequiredArgsConstructor
@Data
@Table(name = "VERSION_ISBN")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "VersionIsbn")
public class VersionIsbn implements Serializable, EbookDefinitionAware {
    private static final long serialVersionUID = 507131831054996893L;

    @Column(name = "VERSION_ISBN_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "VersionIsbnSequence")
    @SequenceGenerator(name = "VersionIsbnSequence", sequenceName = "VERSION_ISBN_ID_SEQ")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
    private BookDefinition ebookDefinition;

    @Column(name = "VERSION")
    @NotNull
    private String version;

    @Column(name = "ISBN")
    @NotNull
    private String isbn;

    public VersionIsbn(final BookDefinition ebookDefinition, final String version, final String isbn) {
        this.ebookDefinition = ebookDefinition;
        this.version = version;
        this.isbn = isbn;
    }
}
