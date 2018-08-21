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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thomsonreuters.uscl.ereader.StringBool;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "PRINT_COMPONENT")
@EqualsAndHashCode
public class PrintComponent implements Serializable {
    private static final long serialVersionUID = -1981084033866475471L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
    private BookDefinition ebookDefinition;

    @Id
    @Column(name = "PRINT_COMPONENT_ID", nullable = false)
    private String printComponentId;

    @Column(name = "COMPONENT_ORDER", nullable = false)
    private int componentOrder;

    @Column(name = "MATERIAL_NUMBER", nullable = false)
    private String materialNumber;

    @Column(name = "COMPONENT_NAME", nullable = false)
    private String componentName;

    @Column(name = "SPLITTER")
    private String splitter;

    /**
     * records whether the component exists in the XPP_BUNDLE_ARCHIVE table, for later display in the generation
     * preview page
     */
    @Transient
    private boolean componentInArchive;

    @Transient
    private boolean isSupplement;

    @JsonIgnore
    public BookDefinition getBookDefinition() {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition) {
        ebookDefinition = bookDefinition;
    }

    public String getPrintComponentId() {
        return printComponentId;
    }

    public void setPrintComponentId(final String printComponentId) {
        this.printComponentId = printComponentId;
    }

    public int getComponentOrder() {
        return componentOrder;
    }

    public void setComponentOrder(final int componentOrder) {
        this.componentOrder = componentOrder;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(final String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(final String componentName) {
        this.componentName = componentName;
    }

    public void setComponentInArchive(final boolean componentInArchive) {
        this.componentInArchive = componentInArchive;
    }

    public boolean getComponentInArchive() {
        return componentInArchive;
    }

    public boolean getSplitter() {
        return StringBool.toBool(splitter);
    }

    public void setSplitter(final boolean splitter) {
        this.splitter = StringBool.toString(splitter);
    }

    public boolean isSupplement() {
        return isSupplement;
    }

    public void setSupplement(final boolean supplement) {
        isSupplement = supplement;
    }
}
