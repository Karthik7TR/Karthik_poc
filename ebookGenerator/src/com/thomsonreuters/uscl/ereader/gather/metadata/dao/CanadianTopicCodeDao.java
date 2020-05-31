package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanadianTopicCodeDao extends JpaRepository<CanadianTopicCode, Long> {
    List<CanadianTopicCode> findAllByJobInstanceId(Long jobInstanceId);
    List<CanadianTopicCode> findAllByJobInstanceIdAndDocUuidOrderByTopicKeyDesc(Long jobInstanceId, String docUuid);
}