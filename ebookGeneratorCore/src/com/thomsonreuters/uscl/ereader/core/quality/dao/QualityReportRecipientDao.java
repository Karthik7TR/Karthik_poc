package com.thomsonreuters.uscl.ereader.core.quality.dao;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualityReportRecipientDao extends JpaRepository<QualityReportRecipient, String> {
}
