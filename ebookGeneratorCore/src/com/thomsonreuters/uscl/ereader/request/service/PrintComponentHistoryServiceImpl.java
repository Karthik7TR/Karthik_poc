package com.thomsonreuters.uscl.ereader.request.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.model.PrintComponentVersion;
import com.thomsonreuters.uscl.ereader.request.dao.PrintComponentHistoryDao;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("printComponentHistoryService")
public class PrintComponentHistoryServiceImpl implements PrintComponentHistoryService {
    @Autowired
    private PrintComponentHistoryDao printComponentHistoryDao;

    @Override
    public List<String> findPrintComponentVersionsList(final Long bookDefinitionId) {
        return printComponentHistoryDao.findPrintComponentVersionsByEbookDefinitionId(bookDefinitionId)
                .stream()
                .map(PrintComponentVersion::new)
                .sorted(Comparator.reverseOrder())
                .map(PrintComponentVersion::toString)
                .collect(Collectors.toList());
    }

    @Override
    public Set<PrintComponentHistory> findPrintComponentByVersion(final Long bookDefinitionId, final String combinedVersion) {
        return printComponentHistoryDao.findPrintComponentByVersion(bookDefinitionId, combinedVersion);
    }

    @Override
    public Optional<Integer> getLatestPrintComponentHistoryVersion(final Long bookDefinitionId, final String eBookDefnVersion) {
        return printComponentHistoryDao.getLatestPrintComponentHistoryVersion(bookDefinitionId, eBookDefnVersion);
    }

    @Override
    @Transactional
    public void savePrintComponents(final Collection<PrintComponent> printComponents, final Long bookDefinitionId,
                                    final String bookDefinitionVersion, final int newPrintComponentHistoryVersion) {
        printComponents.stream().forEach(printComponent -> {
            final PrintComponentHistory printComponentHistory = getPrintComponentHistory(printComponent, bookDefinitionVersion, newPrintComponentHistoryVersion);
            printComponentHistoryDao.save(printComponentHistory);
        });
    }

    private PrintComponentHistory getPrintComponentHistory(final PrintComponent printComponent, final String bookDefinitionVersion, final int newPrintComponentHistoryVersion) {
        final PrintComponentHistory printComponentsHistory = new PrintComponentHistory();
        BeanUtils.copyProperties(printComponent, printComponentsHistory);
        printComponentsHistory.setPrintComponentId(UUID.randomUUID().toString().replaceAll("-", ""));
        printComponentsHistory.setEbookDefinition(printComponent.getBookDefinition());
        printComponentsHistory.setEbookDefinitionVersion(bookDefinitionVersion);
        printComponentsHistory.setPrintComponentVersion(newPrintComponentHistoryVersion);
        return printComponentsHistory;
    }

}
