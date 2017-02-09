package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.bookaudit;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.StringUtils;

/**
 * The form backing object that holds the data the user enters into the Audit book filter HTML form.
 */
public class AdminAuditFilterForm
{
    public static final String FORM_NAME = "adminAuditFilterForm";

    //private static final Logger log = LogManager.getLogger(AdminAuditFilterForm.class);

    private String titleId;
    private String proviewDisplayName;
    private String isbn;

    public AdminAuditFilterForm()
    {
        initialize();
    }

    /**
     * Set all values back to defaults.
     * Used in resetting the form.
     */
    public void initialize()
    {
        populate(null, null, null);
    }

    public void populate(final String titleId, final String proviewDisplayName, final String isbn)
    {
        this.titleId = titleId;
        this.proviewDisplayName = proviewDisplayName;
        this.isbn = isbn;
    }

    public String getProviewDisplayName()
    {
        return proviewDisplayName;
    }

    public String getTitleId()
    {
        return titleId;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setProviewDisplayName(final String name)
    {
        proviewDisplayName = (name != null) ? name.trim() : null;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = (titleId != null) ? titleId.trim() : null;
    }

    public void setIsbn(final String isbn)
    {
        this.isbn = (isbn != null) ? isbn.trim() : null;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean isEmpty()
    {
        return StringUtils.isEmpty(titleId) && StringUtils.isEmpty(proviewDisplayName) && StringUtils.isEmpty(isbn);
    }
}
