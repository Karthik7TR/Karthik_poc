package com.thomsonreuters.uscl.ereader.quality.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;

public interface ComparisonService {
    JsonResponse compare(List<CompareUnit> compareUnitList);
}
