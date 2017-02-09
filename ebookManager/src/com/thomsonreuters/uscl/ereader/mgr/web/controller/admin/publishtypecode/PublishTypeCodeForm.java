package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.publishtypecode;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;

public class PublishTypeCodeForm
{
    //private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
    public static final String FORM_NAME = "pubTypeCodeForm";

    private Long id;
    private String name;

    public PublishTypeCodeForm()
    {
        super();
    }

    public void initialize(final PubTypeCode code)
    {
        id = code.getId();
        name = code.getName();
    }

    public PubTypeCode makeCode()
    {
        final PubTypeCode code = new PubTypeCode();
        code.setId(id);
        code.setName(name);

        return code;
    }

    public Long getPubTypeId()
    {
        return id;
    }

    public void setPubTypeId(final Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }
}
