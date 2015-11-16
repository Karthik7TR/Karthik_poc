package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;

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
	

}
