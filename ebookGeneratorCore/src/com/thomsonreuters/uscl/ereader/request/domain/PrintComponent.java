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
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

@Entity
@Table(name = "PRINT_COMPONENT")
public class PrintComponent implements Serializable
{
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

    @JsonIgnore
    public BookDefinition getBookDefinition()
    {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition)
    {
        ebookDefinition = bookDefinition;
    }

    public String getPrintComponentId()
    {
        return printComponentId;
    }

    public void setPrintComponentId(final String printComponentId)
    {
        this.printComponentId = printComponentId;
    }

    public int getComponentOrder()
    {
        return componentOrder;
    }

    public void setComponentOrder(final int componentOrder)
    {
        this.componentOrder = componentOrder;
    }

    public String getMaterialNumber()
    {
        return materialNumber;
    }

    public void setMaterialNumber(final String materialNumber)
    {
        this.materialNumber = materialNumber;
    }

    public String getComponentName()
    {
        return componentName;
    }

    public void setComponentName(final String componentName)
    {
        this.componentName = componentName;
    }
}
