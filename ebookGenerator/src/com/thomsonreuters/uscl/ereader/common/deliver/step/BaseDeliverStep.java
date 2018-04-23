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
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseDeliverStep extends BookStepImpl implements SplitBookTitlesAwareStep {
    @Resource(name = "proviewHandler")
    private ProviewHandler proviewHandler;
    @Resource(name = "proviewHandlerWithRetry")
    private ProviewHandlerWithRetry proviewHandlerWithRetry;

    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem fileSystem;

    private List<String> publishedSplitTiltes = new ArrayList<>();

    @Override
    public ExitStatus executeStep() throws Exception {
        try {
            publishBook();
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
    }

    private void publishSplitBook() throws ProviewException {
        final Set<String> splitTitleIds = getSplitTitles(
            () -> new TreeSet<>(new SplitTitleIdsComparator(getBookDefinition().getFullyQualifiedTitleId())));
        for (final String splitTitleId : splitTitleIds) {
            final File assembledSplitTitleFile = fileSystem.getAssembledSplitTitleFile(this, splitTitleId);
            proviewHandler.publishTitle(splitTitleId, getBookVersion(), assembledSplitTitleFile);
            publishedSplitTiltes.add(splitTitleId);
        }
    }

    private void removePublishedSplitTitles() throws ProviewException {
        for (final String splitTitle : publishedSplitTiltes) {
            proviewHandlerWithRetry.removeTitle(splitTitle, getBookVersion());
        }
    }
}
