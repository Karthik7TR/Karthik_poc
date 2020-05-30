package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;

import java.util.List;

public interface CanadianTopicCodeService {
    List<CanadianTopicCode> findAllCanadianTopicCodesForTheBook(Long jobInstanceId);
}
