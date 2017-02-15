package com.thomsonreuters.uscl.ereader.proview;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents the cover art for a given title.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class Artwork
{
    private String src;
    private String type = "cover";

    public Artwork()
    {
        //Intentionally left blank
    }

    public Artwork(final String src)
    {
        if (StringUtils.isBlank(src))
        {
            throw new IllegalArgumentException("'src' parameter is required in order to create cover art.");
        }
        this.src = src;
    }

    public String getSrc()
    {
        return src;
    }

    public String getType()
    {
        return type;
    }
}
