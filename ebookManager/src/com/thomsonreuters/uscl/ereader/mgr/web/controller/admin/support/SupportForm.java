package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.support;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

public class SupportForm
{
    //private static final Logger log = LogManager.getLogger(SupportForm.class);
    public static final String FORM_NAME = "supportForm";

    private Long id;
    private String linkDescription;
    private String linkAddress;

    public SupportForm()
    {
        super();
    }

    public void initialize(final SupportPageLink spl)
    {
        id = spl.getId();
        linkDescription = spl.getLinkDescription();
        linkAddress = spl.getLinkAddress();
    }

    public SupportPageLink makeCode()
    {
        final SupportPageLink spl = new SupportPageLink();
        spl.setId(id);
        spl.setLinkAddress(linkAddress);
        spl.setLinkDescription(linkDescription);

        return spl;
    }

    public Long getSupportPageLinkId()
    {
        return id;
    }

    public void setSupportPageLinkId(final Long id)
    {
        this.id = id;
    }

    public String getLinkDescription()
    {
        return linkDescription;
    }

    public void setLinkDescription(final String linkDescription)
    {
        this.linkDescription = linkDescription;
    }

    public String getLinkAddress()
    {
        return linkAddress;
    }

    public void setLinkAddress(final String linkAddress)
    {
        this.linkAddress = linkAddress;
    }

}
