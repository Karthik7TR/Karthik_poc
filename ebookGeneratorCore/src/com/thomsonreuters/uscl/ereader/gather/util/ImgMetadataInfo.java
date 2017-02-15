package com.thomsonreuters.uscl.ereader.gather.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlRootElement(name = "imgMetadataInfo")
public class ImgMetadataInfo
{
    private String mimeType; // image mime-type
    private Long size; // image size in bytes
    private String dimUnit; // dimension units, like "px"
    private Long height; // image height in the dimensional units
    private Long width; // image width in the dimensional units
    private Long dpi; // dots per inch
    private String imgGuid; // image GUID to retrieve
    private String docGuid;

    public ImgMetadataInfo()
    {
    }

    @XmlElement(name = "mimeType", required = false)
    public void setMimeType(final String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    @XmlElement(name = "size", required = false)
    public void setSize(final Long size)
    {
        this.size = size;
    }

    public Long getSize()
    {
        return size;
    }

    @XmlElement(name = "dimUnit", required = false)
    public void setDimUnit(final String dimUnit)
    {
        this.dimUnit = dimUnit;
    }

    public String getDimUnit()
    {
        return dimUnit;
    }

    @XmlElement(name = "height", required = false)
    public void setHeight(final Long height)
    {
        this.height = height;
    }

    public Long getHeight()
    {
        return height;
    }

    @XmlElement(name = "width", required = false)
    public void setWidth(final Long width)
    {
        this.width = width;
    }

    public Long getWidth()
    {
        return width;
    }

    @XmlElement(name = "dpi", required = false)
    public Long getDpi()
    {
        return dpi;
    }

    public void setDpi(final Long dpi)
    {
        this.dpi = dpi;
    }

    @XmlElement(name = "imgGuid", required = false)
    public void setImgGuid(final String imgGuid)
    {
        this.imgGuid = imgGuid;
    }

    public String getImgGuid()
    {
        return imgGuid;
    }

    @XmlElement(name = "docGuid", required = false)
    public void setDocGuid(final String docGuid)
    {
        this.docGuid = docGuid;
    }

    public String getDocGuid()
    {
        return docGuid;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
