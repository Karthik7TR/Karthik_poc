package com.thomsonreuters.uscl.ereader.gather.img.model;

import java.io.File;

public class ImageRequestParameters
{
    private File docToImageManifestFile;
    private File dynamicImageDirectory;
    private boolean isFinalStage;
    private String xppSourceImageDirectory;

    public File getDocToImageManifestFile()
    {
        return docToImageManifestFile;
    }

    public void setDocToImageManifestFile(final File docToImageManifestFile)
    {
        this.docToImageManifestFile = docToImageManifestFile;
    }

    public File getDynamicImageDirectory()
    {
        return dynamicImageDirectory;
    }

    public void setDynamicImageDirectory(final File dynamicImageDirectory)
    {
        this.dynamicImageDirectory = dynamicImageDirectory;
    }

    public boolean isFinalStage()
    {
        return isFinalStage;
    }

    public void setFinalStage(final boolean isFinalStage)
    {
        this.isFinalStage = isFinalStage;
    }

    public String getXppSourceImageDirectory()
    {
        return xppSourceImageDirectory;
    }

    public void setXppSourceImageDirectory(final String xppSourceImageDirectory)
    {
        this.xppSourceImageDirectory = xppSourceImageDirectory;
    }
}
