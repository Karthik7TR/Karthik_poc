package com.thomsonreuters.uscl.ereader.mgr.web.service.form;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GenerateHelperService {
    @Autowired(required = false)
    private MessageSourceAccessor messageSourceAccessor;
    @Autowired
    private ManagerService managerService;
    @Autowired(required = false)
    private JobRequestService jobRequestService;
    @Autowired
    private XppBundleArchiveService xppBundleArchiveService;

    public Optional<String> getError(final BookDefinition book, final GenerateBookForm form) {
        final String queuePriorityLabel = getPriorityLabel(form);
        final boolean jobAlreadyQueued = jobRequestService.isBookInJobRequest(book.getEbookDefinitionId());
        final String version = getVersion(form);

        if (jobAlreadyQueued) {
            final Object[] args =
                {book.getFullyQualifiedTitleId(), queuePriorityLabel, "This book is already in the job queue"};
            return of(getMessage("mesg.job.enqueued.fail", null, args));
        } else if (book.isDeletedFlag()) {
            return of(getMessage("mesg.book.deleted", null));
        } else if (emptyPrintComponents(book)) {
            return of(getMessage("mesg.empty.printcomponents", null));
        } else if (bundlesMissed(book)) {
            return of(getMessage("mesg.missing.bundle", null));
        }

        final JobExecution runningJobExecution = managerService.findRunningJob(book);
        if (runningJobExecution != null) {
            final Object[] args = {book.getFullyQualifiedTitleId(), version, runningJobExecution.getId().toString()};
            return of(getMessage("mesg.job.enqueued.in.progress", null, args));
        }
        return empty();
    }

    public String getPriorityLabel(final GenerateBookForm form) {
        final String key = form.isHighPriorityJob() ? "label.high" : "label.normal";
        return messageSourceAccessor.getMessage(key);
    }

    public String getMessage(final String messageId, final Exception e, final Object... args) {
        final String errMessage = args.length == 0
            ? messageSourceAccessor.getMessage(messageId) : messageSourceAccessor.getMessage(messageId, args);
        if (e != null) {
            log.error(errMessage, e);
        }
        return errMessage;
    }

    public String getVersion(final GenerateBookForm form) {
        switch (form.getNewVersion()) {
        case MAJOR:
            return form.getNewMajorVersion();
        case MINOR:
            return form.getNewMinorVersion();
        case OVERWRITE:
            return form.getNewOverwriteVersion();
        case CUSTOM_VERSION:
            return form.getNewCustomVersion();
        default:
            throw new RuntimeException();
        }
    }

    /**
     * Determine whether the Bundles for the specified print components have been received and saved yet, and
     * set the corresponding field in PrintComponent to display for each.
     *
     * @param book
     * @return
     */
    private boolean bundlesMissed(final BookDefinition book) {
        if (book.getSourceType() != SourceType.XPP) {
            return false;
        }

        final List<String> materialNumbers = book.getPrintComponents()
            .stream()
            .filter(printComponent -> !printComponent.getSplitter())
            .map(PrintComponent::getMaterialNumber)
            .collect(Collectors.toList());
        final Map<String, XppBundleArchive> bundles =
            xppBundleArchiveService.findByMaterialNumberList(materialNumbers).stream().collect(
                Collectors.toMap(
                    XppBundleArchive::getMaterialNumber,
                    Function.identity(),
                    (bundleOne, bundleTwo) -> bundleOne,
                    HashMap::new));
        return !materialNumbers.stream().allMatch(bundles::containsKey);
    }

    private boolean emptyPrintComponents(final BookDefinition book) {
        return book.getSourceType() == SourceType.XPP && book.getPrintComponents().isEmpty();
    }
}
