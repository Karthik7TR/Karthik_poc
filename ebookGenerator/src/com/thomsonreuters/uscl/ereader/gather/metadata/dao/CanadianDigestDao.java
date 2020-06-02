package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanadianDigestDao extends JpaRepository<CanadianDigest, Long> {
    List<CanadianDigest> findAllByJobInstanceId(Long jobInstanceId);
}