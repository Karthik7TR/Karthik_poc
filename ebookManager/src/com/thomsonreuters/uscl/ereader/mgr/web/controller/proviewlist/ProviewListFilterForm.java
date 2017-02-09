package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ProviewListFilterForm
{
    public static final String FORM_NAME = "proviewListFilterForm";

    public enum FilterCommand
    {
        SEARCH,
        RESET
    };

    private String proviewDisplayName;
    private String titleId;
    private String minVersions;
    private String maxVersions;
    private Integer minVersionsInt;
    private Integer maxVersionsInt;
    private FilterCommand filterCommand;

    public void initNull()
    {
        init(null, null, null, null);
    }

    private void init(final String proviewDisplayName, final String titleId, final String minVersions, final String maxVersions)
    {
        this.proviewDisplayName = proviewDisplayName;
        this.titleId = titleId;
        this.minVersions = minVersions;
        this.maxVersions = maxVersions;
    }

    public Integer getMinVersionsInt()
    {
        return minVersionsInt;
    }

    public void setMinVersionsInt(final Integer minVersionsInt)
    {
        this.minVersionsInt = minVersionsInt;
    }

    public Integer getMaxVersionsInt()
    {
        return maxVersionsInt;
    }

    public void setMaxVersionsInt(final Integer maxVersionsInt)
    {
        this.maxVersionsInt = maxVersionsInt;
    }

    public FilterCommand getFilterCommand()
    {
        return filterCommand;
    }

    public void setFilterCommand(final FilterCommand filterCommand)
    {
        this.filterCommand = filterCommand;
    }

    public String getMinVersions()
    {
        return minVersions;
    }

    public void setMinVersions(final String minVersions)
    {
        this.minVersions = minVersions == null ? null : minVersions.trim();
        try
        {
            minVersionsInt = Integer.parseInt(minVersions);
        }
        catch (final NumberFormatException e)
        {
            this.minVersions = null;
            minVersionsInt = 0;
        }
    }

    public String getMaxVersions()
    {
        return maxVersions;
    }

    public void setMaxVersions(final String maxVersions)
    {
        this.maxVersions = maxVersions == null ? null : maxVersions.trim();
        try
        {
            maxVersionsInt = Integer.parseInt(maxVersions);
        }
        catch (final NumberFormatException e)
        {
            this.maxVersions = null;
            maxVersionsInt = 99999;
        }
    }

    public String getProviewDisplayName()
    {
        return proviewDisplayName;
    }

    public void setProviewDisplayName(final String proviewDisplayName)
    {
        this.proviewDisplayName = proviewDisplayName == null ? null : proviewDisplayName.trim();
    }

    public String getTitleId()
    {
        return titleId;
    }

    public void setTitleId(final String titleId)
    {
        this.titleId = titleId == null ? null : titleId.trim();
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
