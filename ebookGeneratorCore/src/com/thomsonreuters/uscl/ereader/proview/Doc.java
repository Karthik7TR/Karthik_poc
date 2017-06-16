package com.thomsonreuters.uscl.ereader.proview;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a doc within title.xml.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc
{
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String src;
    @XmlTransient
    private int splitTitlePart;
    private List<String> imageIdList;

    public Doc()
    {
    }

    public Doc(final String id, final String src, final int splitTitlePart, final List<String> imageIdList)
    {
        if (StringUtils.isBlank(id))
        {
            throw new IllegalArgumentException("'id' is a required field on <doc>.");
        }
        if (StringUtils.isBlank(src))
        {
            throw new IllegalArgumentException("'src' is a required field on <doc>.");
        }
        if (splitTitlePart > 0)
        {
            this.splitTitlePart = splitTitlePart;
        }
        if (imageIdList != null && imageIdList.size() > 0)
        {
            this.imageIdList = imageIdList;
        }
        this.id = id;
        this.src = src;
    }

    public String getSrc()
    {
        return src;
    }

    public String getId()
    {
        return id;
    }

    public int getSplitTitlePart()
    {
        return splitTitlePart;
    }

    public void setSplitTitlePart(final int splitTitlePart)
    {
        this.splitTitlePart = splitTitlePart;
    }

    public List<String> getImageIdList()
    {
        return imageIdList;
    }

    public void setImageIdList(List<String> imageIdList)
    {
        if (imageIdList == null)
        {
            imageIdList = new ArrayList<>();
        }
        this.imageIdList = imageIdList;
    }

    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Doc [id=").append(id).append(", ");
        buffer.append("src=").append(src).append(", ");
        buffer.append("splitTitlePart=").append(splitTitlePart);
        if (imageIdList != null)
            buffer.append(", ").append("imgSize=").append(imageIdList.size()).append(", ");
        buffer.append("]");

        return buffer.toString();
    }
}
