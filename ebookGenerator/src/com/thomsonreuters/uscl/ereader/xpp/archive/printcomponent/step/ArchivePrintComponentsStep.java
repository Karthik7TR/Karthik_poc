package com.thomsonreuters.uscl.ereader.xpp.archive.printcomponent.step;

import java.util.Set;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.service.PrintComponentHistoryService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ArchivePrintComponentsStep extends BookStepImpl {
    @Autowired
    private PrintComponentHistoryService printComponentsHistoryService;

    @Override
    public ExitStatus executeStep() throws Exception {
        final BookDefinition bookDefinition = getBookDefinition();
        final Set<PrintComponent> printComponents = bookDefinition.getPrintComponents();
        final String eBookDefnVersion = getBookVersionString();
        final Long eBookDefinitionId = bookDefinition.getEbookDefinitionId();

        final int newPrintComponentHistoryVersion =
            printComponentsHistoryService
            .getLatestPrintComponentHistoryVersion(eBookDefinitionId, eBookDefnVersion)
            .orElse(-1) + 1;

        printComponentsHistoryService.savePrintComponents(printComponents, eBookDefinitionId, eBookDefnVersion, newPrintComponentHistoryVersion);

        return ExitStatus.COMPLETED;
    }
}
