package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a keyword within title.xml
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Keyword
{
    private String type;
    private String text;

    public Keyword()
    {
    }

    public Keyword(final String type, final String text)
    {
        if (StringUtils.isBlank(type))
        {
            throw new IllegalArgumentException("'type' attribute required for keyword.");
        }
        if (StringUtils.isBlank(text))
        {
            throw new IllegalArgumentException("must provide keyword text.");
        }
        this.type = type;
        this.text = text;
    }

    public String getType()
    {
        return type;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
