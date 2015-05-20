package com.thomsonreuters.uscl.ereader.format.step;


public class DocumentInfo {
	Long docSize;
	String splitTitleId;
	public Long getDocSize() {
		return docSize;
	}

	public void setDocSize(Long docSize) {
		this.docSize = docSize;
	}

	public String getSplitTitleId() {
		return splitTitleId;
	}

	public void setSplitTitleId(String splitTitleId) {
		this.splitTitleId = splitTitleId;
	}
	
	public String toString(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("docSize=[").append(docSize).append("] ");
		buffer.append("splitTitleId=[").append(splitTitleId).append("] ");
		return buffer.toString();
	}

}
