package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PilotBookDao extends JpaRepository<PilotBook, PilotBook.PilotBookPk> {
    PilotBook findOneByPilotBookTitleId(String pilotBookTitleId);

    @Query("from PilotBook pb where pb.ebookDefinition.ebookDefinitionId = :eBookDefnId")
    List<PilotBook> findPilotBooksByEBookDefnId(@Param("eBookDefnId") Long eBookDefnId);
}
