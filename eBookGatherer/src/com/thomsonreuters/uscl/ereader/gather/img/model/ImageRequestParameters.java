package com.thomsonreuters.uscl.ereader.gather.img.model;

import java.io.File;
import java.util.Collection;

public class ImageRequestParameters {
    private File docToImageManifestFile;
    private File dynamicImageDirectory;
    private boolean isFinalStage;
    private Collection<String> xppSourceImageDirectory;

    public File getDocToImageManifestFile() {
        return docToImageManifestFile;
    }

    public void setDocToImageManifestFile(final File docToImageManifestFile) {
        this.docToImageManifestFile = docToImageManifestFile;
    }

    public File getDynamicImageDirectory() {
        return dynamicImageDirectory;
    }

    public void setDynamicImageDirectory(final File dynamicImageDirectory) {
        this.dynamicImageDirectory = dynamicImageDirectory;
    }

    public boolean isFinalStage() {
        return isFinalStage;
    }

    public void setFinalStage(final boolean isFinalStage) {
        this.isFinalStage = isFinalStage;
    }

    public Collection<String> getXppSourceImageDirectory() {
        return xppSourceImageDirectory;
    }

    public void setXppSourceImageDirectory(final Collection<String> xppSourceImageDirectory) {
        this.xppSourceImageDirectory = xppSourceImageDirectory;
    }
}
