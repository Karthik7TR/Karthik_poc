package com.thomsonreuters.uscl.ereader.proview;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a feature within title.xml.
 *
 * <p>The only required field is 'name'. If 'value' is not supplied (single-arg constructor) it will be omitted.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Feature
{
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String value;

    public Feature()
    {
    }

    public Feature(final String name)
    {
        this.name = name;
    }

    public Feature(final String name, final String value)
    {
        if (StringUtils.isBlank(name))
        {
            throw new IllegalArgumentException("'name' parameter is required for all Features.");
        }
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}
