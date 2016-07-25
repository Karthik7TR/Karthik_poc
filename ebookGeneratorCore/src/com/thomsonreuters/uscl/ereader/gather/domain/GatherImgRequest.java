/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="gatherImgRequest")
public class GatherImgRequest {
	private File dynamicImageDirectory;
	private File imgToDocManifestFile;
	private boolean isFinalStage;

	public GatherImgRequest(){
		
	}
	
	public GatherImgRequest(
			File imgToDocManifestFile, File dynamicImageDirectory, long jobInstanceId, boolean isFinalStage){
		this.imgToDocManifestFile = imgToDocManifestFile;
		this.dynamicImageDirectory = dynamicImageDirectory;
		this.isFinalStage = isFinalStage;
	}

	public boolean isFinalStage() {
		return isFinalStage;
	}
	@XmlElement(name="isFinalStage", required = true)
	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}
	
	public File getImgToDocManifestFile() {
		return imgToDocManifestFile;
	}
	@XmlElement(name="imgToDocManifestFile", required=true)
	public void setImgToDocManifestFile(File imgToDocManifestFile) {
		this.imgToDocManifestFile = imgToDocManifestFile;
	}
	
	public File getDynamicImageDirectory() {
		return dynamicImageDirectory;
	}
	@XmlElement(name="dynamicImageDirectory", required=true)
	public void setDynamicImageDirectory(File dynamicImageDirectory) {
		this.dynamicImageDirectory = dynamicImageDirectory;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GatherImgRequest that = (GatherImgRequest) obj;
		if (dynamicImageDirectory == null) {
			if (that.dynamicImageDirectory != null)
				return false;
		} else if (!dynamicImageDirectory.equals(that.dynamicImageDirectory))
			return false;
		if (isFinalStage != that.isFinalStage)
			return false;
		if (imgToDocManifestFile == null) {
			if (that.imgToDocManifestFile != null)
				return false;
		} else if (!imgToDocManifestFile.equals(that.imgToDocManifestFile))
			return false;
		return true;
	} 
}
