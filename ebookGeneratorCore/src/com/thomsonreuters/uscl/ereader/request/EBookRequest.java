package com.thomsonreuters.uscl.ereader.request;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "eBookRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "EBOOK_ARCHIVE")
public class EBookRequest {
	
	public static final String KEY_REQUEST_XML = "ebookrequestXml";
	public static final String KEY_EBOOK_REQUEST = "ebookRequest";
	public static final String JOB_NAME_PROCESS_BUNDLE = "ebookBundleJob";
	public static final String KEY_JOB_NAME = "jobName";

	@Id
	@GeneratedValue(generator = "EBookRequestSequence")
	@SequenceGenerator(name="EBookRequestSequence", sequenceName = "EBOOK_ARCHIVE_ID_SEQ")
	@Column(name = "EBOOK_ARCHIVE_ID", nullable=false)
	private Long ebookArchiveId;
	
	@XmlAttribute(name="version")
	@Transient
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
	private File archiveLocation;
	
	@XmlTransient
	@Column(name = "MESSAGE_REQUEST", nullable=false)
	private String messageRequest;

	@XmlTransient
	@Column(name = "PRODUCT_NAME", nullable=false)
	private String productName = "UNKNOWN";
	
	@XmlTransient
	@Column(name = "PRODUCT_TYPE", nullable=false)
	private String productType = "UNKNOWN";

	@XmlTransient
	@Column(name="RESURRECT_COUNT", nullable=false)
	private int resurrectCount = 0;
	
	@XmlTransient
	@Column(name="DELETED", nullable=false)
	private String isDeleted = "N";
	
	public Long getEBookArchiveId() {
		return ebookArchiveId;
	}
	public void setEBookArchiveId(Long eBookArchiveId) {
		this.ebookArchiveId = eBookArchiveId;
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
		return archiveLocation;
	}
	public void setEBookSrcFile(File eBookSrcFile) {
		this.archiveLocation = eBookSrcFile;
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
		
		if (archiveLocation != null) {
			if (!archiveLocation.equals(that.archiveLocation))
				return false;
		} else if (that.archiveLocation != null)
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
		result = prime * result + ((archiveLocation == null) ? 0 : archiveLocation.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return messageId;
	}
}