package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "EBOOK_DEFINITION_LOCK")
public class BookDefinitionLock implements Serializable {
	private static final long serialVersionUID = 6382455351376387289L;
	
	// LOCK_TIMEOUT is set to equal the session timeout on the eBook Manager 
	public static final int LOCK_TIMEOUT_SEC = 14400; // In seconds

	@Column(name = "EBOOK_DEFINITION_LOCK_ID", nullable = false)
	@Id
	@GeneratedValue(generator = "BookDefinitionLockSequence")
	@SequenceGenerator(name="BookDefinitionLockSequence", sequenceName = "EBOOK_DEFINITION_LOCK_ID_SEQ")
	Long ebookDefinitionLockId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID") })
	BookDefinition ebookDefinition;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHECKOUT_TIMESTAMP", nullable = false)
	Date checkoutTimestamp;
	
	@Column(name = "USERNAME", nullable = false, length = 1024)
	String username;
	
	@Column(name = "FULL_NAME", nullable = false, length = 1024)
	String fullName;

	public Long getEbookDefinitionLockId() {
		return ebookDefinitionLockId;
	}

	public void setEbookDefinitionLockId(Long ebookDefinitionLockId) {
		this.ebookDefinitionLockId = ebookDefinitionLockId;
	}

	public BookDefinition getEbookDefinition() {
		return ebookDefinition;
	}

	public void setEbookDefinition(BookDefinition ebookDefinition) {
		this.ebookDefinition = ebookDefinition;
	}

	public Date getCheckoutTimestamp() {
		return checkoutTimestamp;
	}

	public void setCheckoutTimestamp(Date checkoutTimestamp) {
		this.checkoutTimestamp = checkoutTimestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((checkoutTimestamp == null) ? 0 : checkoutTimestamp
						.hashCode());
		result = prime
				* result
				+ ((ebookDefinitionLockId == null) ? 0 : ebookDefinitionLockId
						.hashCode());
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookDefinitionLock other = (BookDefinitionLock) obj;
		if (checkoutTimestamp == null) {
			if (other.checkoutTimestamp != null)
				return false;
		} else if (!checkoutTimestamp.equals(other.checkoutTimestamp))
			return false;
		if (ebookDefinitionLockId == null) {
			if (other.ebookDefinitionLockId != null)
				return false;
		} else if (!ebookDefinitionLockId.equals(other.ebookDefinitionLockId))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
