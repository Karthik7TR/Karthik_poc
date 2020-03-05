package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Map;

public interface SplitPartsUniteService {

    Map<String, ProviewTitleContainer> getTitlesWithUnitedParts(Map<String, ProviewTitleContainer> allProviewTitleInfo);
}
