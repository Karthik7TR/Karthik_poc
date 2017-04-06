package com.thomsonreuters.uscl.ereader.request.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "PRINT_COMPONENT")
public class PrintComponent implements Serializable
{
    private static final long serialVersionUID = -1981084033866475471L;

    @Id
    @GeneratedValue(generator = "PrintComponentSequence")
    @SequenceGenerator(name = "PrintComponentSequence", sequenceName = "PRINT_COMPONENT_ID_SEQ")
    @Column(name = "PRINT_COMPONENT_ID", nullable = false)
    private String printComponentId;

    @Column(name = "ORDER", nullable = false)
    private int order;

    @Column(name = "MATERIAL_NUMBER", nullable = false)
    private String materialNumber;

    @Column(name = "COMPONENT_NAME", nullable = false)
    private String componentName;

    public String getPrintComponentId()
    {
        return printComponentId;
    }

    public void setPrintComponentId(final String printComponentId)
    {
        this.printComponentId = printComponentId;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(final int order)
    {
        this.order = order;
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
