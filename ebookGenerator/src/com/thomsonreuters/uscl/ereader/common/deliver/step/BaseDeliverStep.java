package com.thomsonreuters.uscl.ereader.common.deliver.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.deliver.comparsion.SplitTitleIdsComparator;
import com.thomsonreuters.uscl.ereader.common.deliver.service.ProviewHandlerWithRetry;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDeliverStep extends BookStepImpl implements SplitBookTitlesAwareStep {
    @Resource(name = "proviewHandler")
    private ProviewHandler proviewHandler;
    @Resource(name = "proviewHandlerWithRetry")
    private ProviewHandlerWithRetry proviewHandlerWithRetry;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem fileSystem;
    @Autowired
    private EBookAuditService eBookAuditService;
    @Autowired
    private VersionIsbnService versionIsbnService;

    private List<String> publishedSplitTitles = new ArrayList<>();

    @Override
    public ExitStatus executeStep() throws Exception {
        try {
            publishBook();
            resetIsbnIfNeeded();
            return ExitStatus.COMPLETED;
        } catch (final ProviewException e) {
            removePublishedSplitTitles();
            throw e;
        }
    }

    private void publishBook() throws ProviewException {
        final BookDefinition bookDefinition = getBookDefinition();
        if (bookDefinition.isSplitBook()) {
            publishSplitBook();
        } else {
            final File assembledBookFile = fileSystem.getAssembledBookFile(this);
            proviewHandler.publishTitle(bookDefinition.getFullyQualifiedTitleId(), getBookVersion(), assembledBookFile);
        }
        versionIsbnService.saveIsbn(bookDefinition, getBookVersionString(), bookDefinition.getIsbn());
        versionIsbnService.saveMaterialId(bookDefinition.getFullyQualifiedTitleId(),getBookVersionString(), bookDefinition.getEntitlement());
    }

    private void publishSplitBook() throws ProviewException {
        final Set<String> splitTitleIds = getSplitTitles(
            () -> new TreeSet<>(new SplitTitleIdsComparator(getBookDefinition().getFullyQualifiedTitleId())));
        for (final String splitTitleId : splitTitleIds) {
            final File assembledSplitTitleFile = fileSystem.getAssembledSplitTitleFile(this, splitTitleId);
            proviewHandler.publishTitle(splitTitleId, getBookVersion(), assembledSplitTitleFile);
            publishedSplitTitles.add(splitTitleId);
        }
    }

    private void removePublishedSplitTitles() throws ProviewException {
        for (final String splitTitle : publishedSplitTitles) {
            proviewHandlerWithRetry.removeTitle(splitTitle, getBookVersion());
        }
    }

    private void resetIsbnIfNeeded() {
        final BookDefinition bookDefinition = getBookDefinition();
        final String titleId = bookDefinition.getFullyQualifiedTitleId();
        final String isbn = bookDefinition.getIsbn();
        if (eBookAuditService.isIsbnModified(titleId, isbn)) {
            eBookAuditService.resetIsbn(titleId, isbn);
        }
    }
}
