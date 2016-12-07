/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.img.model;

import java.io.File;

public class ImageRequestParameters {
	private File docToImageManifestFile;
	private File dynamicImageDirectory;
	private boolean isFinalStage;
	
	public File getDocToImageManifestFile() {
		return docToImageManifestFile;
	}
	
	public void setDocToImageManifestFile(File docToImageManifestFile) {
		this.docToImageManifestFile = docToImageManifestFile;
	}

	public File getDynamicImageDirectory() {
		return dynamicImageDirectory;
	}

	public void setDynamicImageDirectory(File dynamicImageDirectory) {
		this.dynamicImageDirectory = dynamicImageDirectory;
	}

	public boolean isFinalStage() {
		return isFinalStage;
	}

	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}
}
