package com.thomsonreuters.uscl.ereader.core.outage.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "OUTAGE_TYPE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/outage/domain", name = "OutageType")
public class OutageType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "OUTAGE_TYPE_ID", nullable = false)
	@Id
	@SequenceGenerator(name = "outageTypeIdSequence", sequenceName = "OUTAGE_TYPE_ID_SEQ")
	@GeneratedValue(generator = "outageTypeIdSequence")
	private Long id;

	@Column(name = "SYSTEM", nullable = false, length = 128)
	private String system;

	@Column(name = "SUB_SYSTEM", nullable = false, length = 128)
	private String subSystem;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATED", nullable = false)
	private Date lastUpdated;

	@OneToMany
	@Basic(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "OUTAGE_TYPE_ID", referencedColumnName = "OUTAGE_TYPE_ID") })
	private Collection<PlannedOutage> plannedOutage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Collection<PlannedOutage> getPlannedOutage() {
		return plannedOutage;
	}

	public void setPlannedOutage(Collection<PlannedOutage> plannedOutage) {
		this.plannedOutage = plannedOutage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * A more extensive .equals method is nested in the outageEquals(..) method
	 * in JAXBMarshallingTest.java
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutageType other = (OutageType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
