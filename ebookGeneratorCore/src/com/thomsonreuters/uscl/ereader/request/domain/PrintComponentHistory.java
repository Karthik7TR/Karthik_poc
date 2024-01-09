package com.thomsonreuters.uscl.ereader.request.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thomsonreuters.uscl.ereader.StringBool;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import lombok.Data;

@Data
@Entity
@Table(name = "PRINT_COMPONENT_HISTORY")
public class PrintComponentHistory implements Serializable {
    private static final long serialVersionUID = -1981084033866475472L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
    private BookDefinition ebookDefinition;

    @Id
    @Column(name = "PRINT_COMPONENT_ID", nullable = false, length = 33)
    private String printComponentId;

    @JsonIgnore
    @Column(name = "EBOOK_DEFINITION_VERSION", nullable = false, length = 10)
    private String ebookDefinitionVersion;

    @JsonIgnore
    @Column(name = "PRINT_COMPONENT_VERSION", nullable = false)
    private int printComponentVersion;

    @Column(name = "COMPONENT_ORDER", nullable = false)
    private int componentOrder;

    @Column(name = "MATERIAL_NUMBER", nullable = false, length = 64)
    private String materialNumber;

    @Column(name = "COMPONENT_NAME", nullable = false, length = 1024)
    private String componentName;

    @Column(name = "SPLITTER", length = 1)
    private String splitter;

    public boolean getSplitter() {
        return StringBool.toBool(splitter);
    }

    public void setSplitter(final boolean splitter) {
        this.splitter = StringBool.toString(splitter);
    }
}
