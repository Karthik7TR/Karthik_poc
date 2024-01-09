package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XppBundleArchiveDao extends JpaRepository<XppBundleArchive, Long> {

    XppBundleArchive findFirstByMessageId(String messageId);

    XppBundleArchive findFirstByMaterialNumberOrderByDateTimeDesc(String materialNumber);

    List<XppBundleArchive> findByMaterialNumberIn(List<String> sourceMaterialNumberList);
}
