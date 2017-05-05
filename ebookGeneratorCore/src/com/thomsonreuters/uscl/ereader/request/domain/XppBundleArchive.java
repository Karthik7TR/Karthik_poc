package com.thomsonreuters.uscl.ereader.request.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class is the POJO representation of the xml notification sent by Pheonix via Java Message Queue.
 *
 * Not to be confused with XppBundle, which is the POJO version of the information stored in
 * bundle.xml, the metadata file inside the .tar.gz bundle housing the source files for an XPP job.
 */

@XmlRootElement(name = "eBookRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "XPP_BUNDLE_ARCHIVE")
public class XppBundleArchive implements Serializable
{
    private static final long serialVersionUID = -5662203902532139594L;

    @XmlTransient
    @Id
    @GeneratedValue(generator = "XppBundleSequence")
    @SequenceGenerator(name = "XppBundleSequence", sequenceName = "XPP_BUNDLE_ARCHIVE_ID_SEQ")
    @Column(name = "XPP_BUNDLE_ARCHIVE_ID", nullable = false)
    private Long xppBundleArchiveId;

    @XmlAttribute(name = "version")
    @Transient
    private String version;

    @XmlElement(name = "messageId")
    @Column(name = "MESSAGE_ID", nullable = false)
    private String messageId;

    @XmlElement(name = "bundleHash")
    @Transient
    private String bundleHash;

    @XmlElement(name = "dateTime")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_TIME", nullable = false)
    private Date dateTime;

    @XmlElement(name = "srcFile")
    @Column(name = "ARCHIVE_LOCATION", nullable = false)
    private String archiveLocation;

    @XmlTransient
    @Column(name = "MESSAGE_REQUEST", nullable = false)
    private String messageRequest;

    @XmlElement(name = "materialNumber")
    @Column(name = "MATERIAL_NUMBER", nullable = false)
    private String materialNumber;

    @XmlTransient
    @Column(name = "RESURRECT_COUNT", nullable = false)
    private int resurrectCount = 0;

    @XmlTransient
    @Column(name = "DELETED", nullable = false)
    private String isDeleted = "N";

    public Long getXppBundleArchiveId()
    {
        return xppBundleArchiveId;
    }

    public void setXppBundleArchiveId(final Long eBookArchiveId)
    {
        xppBundleArchiveId = eBookArchiveId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(final String version)
    {
        this.version = version;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public void setMessageId(final String messageId)
    {
        this.messageId = messageId;
    }

    public String getBundleHash()
    {
        return bundleHash;
    }

    public void setBundleHash(final String bundleHash)
    {
        this.bundleHash = bundleHash;
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(final Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getEBookSrcPath()
    {
        return archiveLocation;
    }

    public void setEBookSrcPath(final String path)
    {
        archiveLocation = path;
    }

    public File getEBookSrcFile()
    {
        return archiveLocation == null ? null : new File(archiveLocation);
    }

    public void setEBookSrcFile(final File eBookSrcFile)
    {
        archiveLocation = eBookSrcFile.getAbsolutePath();
    }

    public String getMessageRequest()
    {
        return messageRequest;
    }

    public void setMessageRequest(final String messageRequest)
    {
        this.messageRequest = messageRequest;
    }

    public String getMaterialNumber()
    {
        return materialNumber;
    }

    public void setMaterialNumber(final String materialNumber)
    {
        this.materialNumber = materialNumber;
    }

    public int getResurrectionCount()
    {
        return resurrectCount;
    }

    public void setResurrectCount(final int resurrectCount)
    {
        this.resurrectCount = resurrectCount;
    }

    public boolean isDeleted()
    {
        return ((isDeleted.equalsIgnoreCase("Y") ? true : false));
    }

    public void setIsDeleted(final boolean isDeleted)
    {
        this.isDeleted = ((isDeleted) ? "Y" : "N");
    }

    public boolean isSimilar(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;
        final XppBundleArchive that = (XppBundleArchive) obj;

        if (version != null)
        {
            if (!version.equals(that.version))
                return false;
        }
        else if (that.version != null)
            return false;

        if (messageId != null)
        {
            if (!messageId.equals(that.messageId))
                return false;
        }
        else if (that.messageId != null)
            return false;

        if (bundleHash != null)
        {
            if (!bundleHash.equals(that.bundleHash))
                return false;
        }
        else if (that.bundleHash != null)
            return false;

        if (dateTime != null)
        {
            if (!dateTime.equals(that.dateTime))
                return false;
        }
        else if (that.dateTime != null)
            return false;

        if (archiveLocation != null)
        {
            if (!archiveLocation.equals(that.archiveLocation))
                return false;
        }
        else if (that.archiveLocation != null)
            return false;

        return true;
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
        return messageId;
    }
}
