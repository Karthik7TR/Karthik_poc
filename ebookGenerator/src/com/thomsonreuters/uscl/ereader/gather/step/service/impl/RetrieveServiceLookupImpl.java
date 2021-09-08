package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveServiceLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType.FILE;
import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType.NORT;
import static com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType.TOC;

@Service
public class RetrieveServiceLookupImpl implements RetrieveServiceLookup {
    private static final String NOT_SUPPORTED_SOURCE_TYPE = "Not supported source type";
    private final Map<BookDefinition.SourceType, RetrieveService> retrieveServiceMap = new HashMap<>();

    @Autowired
    public RetrieveServiceLookupImpl(@Qualifier("cwbRetrieveService") final RetrieveService cwbRetrieveService,
                                     @Qualifier("nortRetrieveService") final RetrieveService nortRetrieveService,
                                     @Qualifier("tocRetrieveService") final RetrieveService tocRetrieveService) {
        retrieveServiceMap.put(TOC, tocRetrieveService);
        retrieveServiceMap.put(NORT, nortRetrieveService);
        retrieveServiceMap.put(FILE, cwbRetrieveService);
    }

    @Override
    public RetrieveService getRetrieveService(final BookDefinition.SourceType sourceType) {
        return Optional.ofNullable(retrieveServiceMap.get(sourceType))
                .orElseThrow(() -> new EBookException(NOT_SUPPORTED_SOURCE_TYPE));
    }
}
