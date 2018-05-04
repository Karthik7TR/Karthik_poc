package com.thomsonreuters.uscl.ereader.request.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao for PrintComponentHistory
 */
@Transactional(readOnly = true, value = "jpaTransactionManager")
public interface PrintComponentHistoryDao extends JpaRepository<PrintComponentHistory, Long> {
    @Query("select distinct concat(concat(a.ebookDefinitionVersion, '.'), a.printComponentVersion) from PrintComponentHistory a where a.ebookDefinition.ebookDefinitionId = :bookDefinitionId")
    List<String> findPrintComponentVersionsByEbookDefinitionId(@Param("bookDefinitionId") Long bookDefinitionId);

    @Query("from PrintComponentHistory a where a.ebookDefinition.ebookDefinitionId = :bookDefinitionId and concat(concat(a.ebookDefinitionVersion, '.'), a.printComponentVersion) = :combinedVersion")
    Set<PrintComponentHistory> findPrintComponentByVersion(@Param("bookDefinitionId") Long bookDefinitionId, @Param("combinedVersion") String combinedVersion);

    @Query("select max(a.printComponentVersion) from PrintComponentHistory a where a.ebookDefinition.ebookDefinitionId = :bookDefinitionId and a.ebookDefinitionVersion = :bookDefinitionVersion")
    Optional<Integer> getLatestPrintComponentHistoryVersion(@Param("bookDefinitionId") Long bookDefinitionId, @Param("bookDefinitionVersion") String bookDefinitionVersion);
}
