package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents an asset within title.xml
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Asset
{
    private String id;
    private String src;

    public Asset()
    {
    }

    public Asset(final String id, final String src)
    {
        if (StringUtils.isBlank(id))
        {
            throw new IllegalArgumentException("'id' is a required field.");
        }
        if (StringUtils.isBlank(src))
        {
            throw new IllegalArgumentException("'src' is a required field.");
        }
        this.id = id;
        this.src = src;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getId()
    {
        return id;
    }

    public String getSrc()
    {
        return src;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
            return true;
        if (!(obj instanceof Asset))
            return false;
        final Asset equalCheck = (Asset) obj;
        if ((id == null && equalCheck.id != null) || (id != null && equalCheck.id == null))
            return false;
        if (id != null && !id.equals(equalCheck.id))
            return false;
        if ((src == null && equalCheck.src != null) || (src != null && equalCheck.src == null))
            return false;
        if (src != null && !src.equals(equalCheck.src))
            return false;
        return true;
    }
}
