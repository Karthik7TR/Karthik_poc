package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.outage;

import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;

public class OutageTypeForm
{
    //private static final Logger log = LogManager.getLogger(OutageTypeForm.class);
    public static final String FORM_NAME = "outageTypeForm";

    private Long outageTypeId;
    private String system;
    private String subSystem;

    public OutageTypeForm()
    {
        super();
    }

    public void initialize(final OutageType outageType)
    {
        outageTypeId = outageType.getId();
        system = outageType.getSystem();
        subSystem = outageType.getSubSystem();
    }

    public OutageType createOutageType()
    {
        final OutageType outageType = new OutageType();
        outageType.setId(outageTypeId);
        outageType.setSystem(system);
        outageType.setSubSystem(subSystem);
        return outageType;
    }

    public Long getOutageTypeId()
    {
        return outageTypeId;
    }

    public void setOutageTypeId(final Long id)
    {
        outageTypeId = id;
    }

    public String getSystem()
    {
        return system;
    }

    public void setSystem(final String system)
    {
        this.system = system;
    }

    public String getSubSystem()
    {
        return subSystem;
    }

    public void setSubSystem(final String subSystem)
    {
        this.subSystem = subSystem;
    }
}
