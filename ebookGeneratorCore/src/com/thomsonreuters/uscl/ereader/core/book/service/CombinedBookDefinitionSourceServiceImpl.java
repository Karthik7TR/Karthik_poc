package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.dao.CombinedBookDefinitionSourceDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CombinedBookDefinitionSourceServiceImpl implements CombinedBookDefinitionSourceService {
    private static final String Y = "Y";
    private final CombinedBookDefinitionSourceDao combinedBookDefinitionSourceDao;

    @Override
    public Optional<CombinedBookDefinitionSource> findPrimarySourceWithBookDefinition(long ebookDefinitionId) {
        return combinedBookDefinitionSourceDao.findCombinedBookDefinitionSourceByIsPrimarySourceAndBookDefinition_EbookDefinitionId(Y, ebookDefinitionId);
    }
}
