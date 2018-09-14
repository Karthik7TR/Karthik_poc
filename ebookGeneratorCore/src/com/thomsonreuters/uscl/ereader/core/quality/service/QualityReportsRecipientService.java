package com.thomsonreuters.uscl.ereader.core.quality.service;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;

import java.util.List;

public interface QualityReportsRecipientService {
    List<QualityReportRecipient> getAll();

    QualityReportRecipient save(String email);

    void delete(String email);
}
