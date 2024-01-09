package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;

import java.util.List;

public interface CanadianDigestService {
    List<CanadianDigest> findAllByJobInstanceId(Long jobInstanceId);
}