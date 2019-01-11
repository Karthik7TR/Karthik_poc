package com.thomsonreuters.uscl.ereader.core.quality.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualityReportParams {
    private boolean qualityStepEnabled;
    private List<String> recipients;
}
