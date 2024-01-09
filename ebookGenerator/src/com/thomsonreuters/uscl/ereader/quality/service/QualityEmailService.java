package com.thomsonreuters.uscl.ereader.quality.service;

import com.thomsonreuters.uscl.ereader.quality.domain.email.QualityReportEmail;

public interface QualityEmailService {
    void sendReportEmail(final QualityReportEmail email);
}
