package com.thomsonreuters.uscl.ereader.core.quality.service;

import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;

public interface QualityReportsAdminService {
    QualityReportParams getParams();

    QualityReportRecipient save(String email);

    void delete(String email);

    void changeQualityStepEnableParameter(boolean isEnable);
}
