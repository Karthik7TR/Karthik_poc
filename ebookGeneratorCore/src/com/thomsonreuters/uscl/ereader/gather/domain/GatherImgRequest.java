package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gatherImgRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherImgRequest
{
    @XmlElement(name = "dynamicImageDirectory")
    private File dynamicImageDirectory;

    @XmlElement(name = "imgToDocManifestFile")
    private File imgToDocManifestFile;

    @XmlElement(name = "isFinalStage")
    private boolean isFinalStage;

    @XmlElement(name = "isXpp")
    private boolean isXpp;

    @XmlElement(name = "xppSourceImageDirectory")
    private Collection<String> xppSourceImageDirectory;

    public GatherImgRequest()
    {
    }

    public GatherImgRequest(
        final File imgToDocManifestFile,
        final File dynamicImageDirectory,
        final long jobInstanceId,
        final boolean isFinalStage)
    {
        this.imgToDocManifestFile = imgToDocManifestFile;
        this.dynamicImageDirectory = dynamicImageDirectory;
        this.isFinalStage = isFinalStage;
    }

    public boolean isFinalStage()
    {
        return isFinalStage;
    }

    public void setFinalStage(final boolean isFinalStage)
    {
        this.isFinalStage = isFinalStage;
    }

    public File getImgToDocManifestFile()
    {
        return imgToDocManifestFile;
    }

    public void setImgToDocManifestFile(final File imgToDocManifestFile)
    {
        this.imgToDocManifestFile = imgToDocManifestFile;
    }

    public File getDynamicImageDirectory()
    {
        return dynamicImageDirectory;
    }

    public void setDynamicImageDirectory(final File dynamicImageDirectory)
    {
        this.dynamicImageDirectory = dynamicImageDirectory;
    }

    public boolean isXpp()
    {
        return isXpp;
    }

    public void setXpp(final boolean isXpp)
    {
        this.isXpp = isXpp;
    }

    public Collection<String> getXppSourceImageDirectory()
    {
        return xppSourceImageDirectory;
    }

    public void setXppSourceImageDirectory(final Collection<String> xppSourceImageDirectory)
    {
        this.xppSourceImageDirectory = xppSourceImageDirectory;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GatherImgRequest that = (GatherImgRequest) obj;
        if (dynamicImageDirectory == null)
        {
            if (that.dynamicImageDirectory != null)
                return false;
        }
        else if (!dynamicImageDirectory.equals(that.dynamicImageDirectory))
            return false;
        if (isFinalStage != that.isFinalStage)
            return false;
        if (imgToDocManifestFile == null)
        {
            if (that.imgToDocManifestFile != null)
                return false;
        }
        else if (!imgToDocManifestFile.equals(that.imgToDocManifestFile))
            return false;

        return Objects.equals(isXpp, that.isXpp)
            && equals(xppSourceImageDirectory, that.xppSourceImageDirectory);
    }

    private static boolean equals(final Collection<String> lhs, final Collection<String> rhs)
    {
        return lhs == null && rhs == null || lhs != null && rhs != null && lhs.size() == rhs.size() && lhs.containsAll(rhs) && rhs.containsAll(lhs);
    }
}
