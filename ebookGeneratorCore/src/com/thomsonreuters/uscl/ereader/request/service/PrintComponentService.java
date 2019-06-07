package com.thomsonreuters.uscl.ereader.request.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.dao.PrintComponentDao;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class PrintComponentService {
    private PrintComponentDao printComponentDao;

    public PrintComponentService(PrintComponentDao printComponentDao) {
        this.printComponentDao = printComponentDao;
    }

    @Transactional
    public PrintComponent saveRequest(final PrintComponent ebookRequest) {
        return printComponentDao.save(ebookRequest);
    }

    public PrintComponent findByPrimaryKey(final long printComponentId) {
        return printComponentDao.findOne(printComponentId);
    }

    @Transactional
    public void deleteComponent(final long printComponentId) {
        printComponentDao.delete(printComponentId);
    }

    public PrintComponent findByComponentName(final String messageId) {
        return printComponentDao.findFirstByComponentName(messageId);
    }

    public PrintComponent findByMaterialNumber(final String materialNumber) {
        return printComponentDao.findFirstByMaterialNumber(materialNumber);
    }

    public List<PrintComponent> findAllRequests() {
        return printComponentDao.findAll();
    }
}
