package com.thomsonreuters.uscl.ereader.jms.handler;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "eBookRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class EBookRequest {
	
	@XmlAttribute(name="version")
	private String version;
	
	@XmlElement(name = "messageId")
	private String messageId;
	
	@XmlElement(name = "bundleHash")
	private String bundleHash;
	
	@XmlElement(name = "dateTime")
	private Date dateTime;
	
	@XmlElement(name = "srcFile")
	private File eBookSrcFile;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getBundleHash() {
		return bundleHash;
	}
	public void setBundleHash(String bundleHash) {
		this.bundleHash = bundleHash;
	}
	
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	public File getEBookSrcFile() {
		return eBookSrcFile;
	}
	public void setEBookSrcFile(File eBookSrcFile) {
		this.eBookSrcFile = eBookSrcFile;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		EBookRequest that = (EBookRequest) obj;
		
		if (version != null) {
			if (!version.equals(that.version))
				return false;
		} else if (that.version != null)
			return false;
		
		if (messageId != null) {
			if (!messageId.equals(that.messageId))
				return false;
		} else if (that.messageId != null)
			return false;
		
		if (bundleHash != null) {
			if (!bundleHash.equals(that.bundleHash))
				return false;
		} else if (that.bundleHash != null)
			return false;
		
		if (dateTime != null) {
			if (!dateTime.equals(that.dateTime))
				return false;
		} else if (that.dateTime != null)
			return false;
		
		if (eBookSrcFile != null) {
			if (!eBookSrcFile.equals(that.eBookSrcFile))
				return false;
		} else if (that.eBookSrcFile != null)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((bundleHash == null) ? 0 : bundleHash.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((eBookSrcFile == null) ? 0 : eBookSrcFile.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return messageId;
	}
}