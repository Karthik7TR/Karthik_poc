package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gatherImgRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GatherImgRequest {
	@XmlElement(name = "dynamicImageDirectory")
	private File dynamicImageDirectory;

	@XmlElement(name = "imgToDocManifestFile")
	private File imgToDocManifestFile;

	@XmlElement(name = "isFinalStage")
	private boolean isFinalStage;

	public GatherImgRequest() {

	}

	public GatherImgRequest(File imgToDocManifestFile, File dynamicImageDirectory, long jobInstanceId, boolean isFinalStage) {
		this.imgToDocManifestFile = imgToDocManifestFile;
		this.dynamicImageDirectory = dynamicImageDirectory;
		this.isFinalStage = isFinalStage;
	}

	public boolean isFinalStage() {
		return isFinalStage;
	}

	public void setFinalStage(boolean isFinalStage) {
		this.isFinalStage = isFinalStage;
	}

	public File getImgToDocManifestFile() {
		return imgToDocManifestFile;
	}

	public void setImgToDocManifestFile(File imgToDocManifestFile) {
		this.imgToDocManifestFile = imgToDocManifestFile;
	}

	public File getDynamicImageDirectory() {
		return dynamicImageDirectory;
	}

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
