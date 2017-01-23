package com.thomsonreuters.uscl.ereader.jms.handler;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement(name = "eBookRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "EBOOK_ARCHIVE")
public class EBookRequest {
	
	@Id @GeneratedValue
	@Column(name = "BUNDLE_ARCHIVE_ID", nullable=false)
	private String bundleArchiveId;
	
	@XmlAttribute(name="version")
	@Column(name = "VERSION", nullable=false)
	private String version;
	
	@XmlElement(name = "messageId")
	@Column(name = "MESSAGE_ID", nullable=false)
	private String messageId;
	
	@XmlElement(name = "bundleHash")
	@Transient
	private String bundleHash;
	
	@XmlElement(name = "dateTime")
	@Column(name = "DATE_TIME", nullable=false)
	private Date dateTime;
	
	@XmlElement(name = "srcFile")
	@Column(name = "ARCHIVE_LOCATION", nullable=false)
	private File eBookSrcFile;
	
	@XmlTransient
	@Column(name = "MESSAGE_REQUEST", nullable=false)
	private String messageRequest;
	
	@XmlTransient
	@Column(name = "PRODUCT_NAME", nullable=false)
	private String productName;
	
	@XmlTransient
	@Column(name = "PRODUCT_TYPE", nullable=false)
	private String productType;
	
	@XmlTransient
	@Column(name="RESURRECT_COUNT", nullable=false)
	private int resurrectCount;
	
	@XmlTransient
	@Column(name="DELETED", nullable=false)
	private String isDeleted;
	
	public String getEBookArchiveId() {
		return bundleArchiveId;
	}
	public void setEBookArchiveId(String eBookArchiveId) {
		this.bundleArchiveId = eBookArchiveId;
	}
	
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
	
	public String getMessageRequest() {
		return messageRequest;
	}
	public void setMessageRequest(String messageRequest) {
		this.messageRequest = messageRequest;
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}

	public int getResurrectionCount() {
		return resurrectCount;
	}
	public void setResurrectCount(int resurrectCount) {
		this.resurrectCount = resurrectCount;
	}

	public boolean isDeleted() {
		return ((this.isDeleted.equalsIgnoreCase("Y") ? true : false));		
	}
	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = ( (isDeleted) ? "Y" : "N");		
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