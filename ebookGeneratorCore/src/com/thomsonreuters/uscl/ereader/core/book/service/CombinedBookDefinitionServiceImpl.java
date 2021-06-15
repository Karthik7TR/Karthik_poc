package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.dao.CombinedBookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CombinedBookDefinitionServiceImpl implements CombinedBookDefinitionService {
    private final CombinedBookDefinitionDao combinedBookDefinitionDao;

    @Autowired
    public CombinedBookDefinitionServiceImpl(final CombinedBookDefinitionDao combinedBookDefinitionDao) {
        this.combinedBookDefinitionDao = combinedBookDefinitionDao;
    }

    @Override
    public List<CombinedBookDefinition> findAllCombinedBookDefinitions() {
        return combinedBookDefinitionDao.findAll();
    }

    @Override
    public CombinedBookDefinition findCombinedBookDefinitionById(final Long id) {
        return combinedBookDefinitionDao.findOne(id);
    }

    @Override
    public CombinedBookDefinition saveCombinedBookDefinition(final CombinedBookDefinition combinedBookDefinition) {
        return combinedBookDefinitionDao.save(combinedBookDefinition);
    }

    @Override
    public void updateDeletedStatus(final Long id, final boolean isDeleted) {
        Optional.ofNullable(findCombinedBookDefinitionById(id))
                .ifPresent(book -> {
                    book.setIsDeletedFlag(isDeleted);
                    combinedBookDefinitionDao.save(book);
                });
    }

    @Override
    public void deleteCombinedBookDefinition(final CombinedBookDefinition combinedBookDefinition) {
        combinedBookDefinitionDao.delete(combinedBookDefinition);
    }
}
