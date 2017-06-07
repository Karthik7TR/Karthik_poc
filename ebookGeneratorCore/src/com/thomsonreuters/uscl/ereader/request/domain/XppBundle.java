package com.thomsonreuters.uscl.ereader.request.domain;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class is the POJO version of the information stored in bundle.xml, the metadata file inside
 * the .tar.gz bundle housing the source files for an XPP job.
 *
 *  Not to be confused with XppBundleArchive, which is the POJO representation of the xml notification
 *  sent by Pheonix via Java Message Queue.
 */

@XmlRootElement(name = "bundle")
@XmlAccessorType(XmlAccessType.FIELD)
public class XppBundle
{
    @XmlElement(name = "product_title")
    private String productTitle;
    @XmlElement(name = "product_type")
    private String productType;
    @XmlElement(name = "material_no")
    private String materialNumber;
    @XmlElement(name = "release_date")
    private Date releaseDate;
    @XmlElement(name = "release_number")
    private int releaseNumber;
    @XmlElement(name = "volumes")
    private int volumes;
    @XmlElement(name = "bundle_root")
    private String bundleRoot;
    @XmlElementWrapper(name = "file_order")
    @XmlElement(name = "file")
    private List<String> orderedFileList;

    public String getProductTitle()
    {
        return productTitle;
    }

    public void setProductTitle(final String productTitle)
    {
        this.productTitle = productTitle;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(final String productType)
    {
        this.productType = productType;
    }

    public String getMaterialNumber()
    {
        return materialNumber;
    }

    public void setMaterialNumber(final String materialNumber)
    {
        this.materialNumber = materialNumber;
    }

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(final Date releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public int getReleaseNumber()
    {
        return releaseNumber;
    }

    public void setReleaseNumber(final int releaseNumber)
    {
        this.releaseNumber = releaseNumber;
    }

    public int getVolumes()
    {
        return volumes;
    }

    public void setVolumes(final int volumes)
    {
        this.volumes = volumes;
    }

    public String getBundleRoot()
    {
        return bundleRoot;
    }

    public void setBundleRoot(final String bundleRoot)
    {
        this.bundleRoot = bundleRoot;
    }

    public List<String> getOrderedFileList()
    {
        return orderedFileList;
    }

    public void setOrderedFileList(final List<String> orderedFileList)
    {
        this.orderedFileList = orderedFileList;
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
