package com.thomsonreuters.uscl.ereader.quality.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.model.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.model.response.JsonResponse;

public interface ComparisonService {
    JsonResponse compare(List<CompareUnit> compareUnitList, String emails);
}
