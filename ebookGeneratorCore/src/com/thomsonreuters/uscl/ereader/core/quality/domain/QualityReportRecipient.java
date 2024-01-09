package com.thomsonreuters.uscl.ereader.core.quality.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "QUALITY_REPORTS_RECIPIENT")
public class QualityReportRecipient {
    @Id
    @Email
    @Column(name = "EMAIL")
    private String email;
}
